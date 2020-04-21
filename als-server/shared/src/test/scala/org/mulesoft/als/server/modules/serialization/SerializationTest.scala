package org.mulesoft.als.server.modules.serialization

import java.io.StringWriter

import amf.client.remote.Content
import amf.core.CompilerContextBuilder
import amf.core.model.document.Document
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Amf, Mimes}
import amf.core.services.RuntimeCompiler
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server._
import org.mulesoft.als.server.feature.serialization.{SerializationClientCapabilities, SerializationParams}
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.yaml.builder.{DocBuilder, JsonOutputBuilder}

import scala.concurrent.{ExecutionContext, Future}

class SerializationTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val alsClient = new MockAlsClientNotifier
  protected val factoryBuilder: WorkspaceManagerFactoryBuilder =
    new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)
  private val serializationProps: SerializationProps[StringWriter] = new SerializationProps[StringWriter](alsClient) {
    override def newDocBuilder(): DocBuilder[StringWriter] = JsonOutputBuilder()
  }
  protected val serializationManager: SerializationManager[StringWriter] =
    factoryBuilder.serializationManager(serializationProps)
  protected val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

  override val initializeParams: AlsInitializeParams = AlsInitializeParams(
    Some(AlsClientCapabilities(serialization = Some(SerializationClientCapabilities(true)))),
    Some(TraceKind.Off))

  test("Parse Model and check serialized json ld notification") {
    withServer { server =>
      val content =
        """#%RAML 1.0
          |title: test
          |description: missing title
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)

      for {
        s <- alsClient.nextCall.map(_.model.toString)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] = Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(api, platform).withEnvironment(env).build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
      }
    }
  }

  test("Request serialized model") {
    withServer { server =>
      val content =
        """#%RAML 1.0
          |title: test
          |description: missing title
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)

      for {
        _ <- alsClient.nextCall.map(_.model.toString)
        s <- serialized(server, api)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] = Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(api, platform).withEnvironment(env).build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
      }
    }
  }

  test("Request serialized model twice and change") {
    withServer { server =>
      val content =
        """#%RAML 1.0
          |title: test
          |description: missing title
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)

      for {
        _       <- alsClient.nextCall.map(_.model.toString)
        s       <- serialized(server, api)
        parsed  <- parsedApi(api, s)
        s2      <- serialized(server, api)
        parsed2 <- parsedApi(api, s2)
        s3 <- {
          changeFile(server)(api, "", 1)
          serialized(server, api)
        }
        parsed3 <- parsedApi(api, s3)
        s4 <- {
          changeFile(server)(api, content, 2)
          serialized(server, api)
        }
        parsed4 <- parsedApi(api, s4)
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
        parsed2.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
        parsed3.isInstanceOf[Document] should be(false)
        parsed4.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")

      }
    }
  }

  private def serialized(server: LanguageServer, api: String) = {
    server
      .resolveHandler(serializationProps.requestType)
      .value
      .apply(SerializationParams(TextDocumentIdentifier(api)))
      .map(_.model.toString)
  }

  private def parsedApi(api: String, s: String) = {

    val env = Environment(new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = Future.successful(new Content(s, api))

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean = resource == api
    })
    RuntimeCompiler
      .forContext(
        new CompilerContextBuilder(api, platform).withEnvironment(env).build(),
        Some(Mimes.`APPLICATION/LD+JSONLD`),
        Some(Amf.name),
        UnspecifiedReference
      )

  }

  test("basic test") {
    withServer { server =>
      val url = filePath("raml-endpoint-sorting.raml")

      for {
        _ <- platform.resolve(url).map { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
        s <- serialized(server, url)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] = Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(url, platform).withEnvironment(env).build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
      }
    }
  }

  test("two requests") {
    withServer { server =>
      val url = filePath("raml-endpoint-sorting.raml")

      for {
        _ <- platform.resolve(url).map { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
        s <- serialized(server, url)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] = Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(url, platform).withEnvironment(env).build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
        s2 <- serialized(server, url)
        parsed2 <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] = Future.successful(new Content(s2, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(url, platform).withEnvironment(env).build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
        parsed2.asInstanceOf[Document].encodes.id should be("amf://id#/web-api")
      }
    }
  }

  override def buildServer(): LanguageServer = {
    val builder =
      new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.resolutionTaskManager)
    builder.addInitializableModule(serializationManager)
    builder.addRequestModule(serializationManager)
    builder.build()
  }

  override def rootPath: String = "serialization"
}
