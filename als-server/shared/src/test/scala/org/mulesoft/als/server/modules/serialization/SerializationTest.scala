package org.mulesoft.als.server.modules.serialization

import amf.client.parse.IgnoringErrorHandler
import amf.client.remote.Content
import amf.core.CompilerContextBuilder
import amf.core.model.document.Document
import amf.core.parser.UnspecifiedReference
import amf.core.remote.{Amf, Mimes}
import amf.core.services.RuntimeCompiler
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.common.diff.Diff.makeString
import org.mulesoft.als.common.diff.{Diff, Tests}
import org.mulesoft.als.server._
import org.mulesoft.als.server.feature.serialization.{SerializationClientCapabilities, SerializationParams}
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.protocol.textsync.DidFocusParams
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{TextDocumentIdentifier, TextDocumentItem}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams
import org.yaml.builder.{DocBuilder, JsonOutputBuilder}
import org.yaml.model.{YDocument, YMap, YSequence}
import org.yaml.parser.YamlParser

import java.io.StringWriter
import scala.concurrent.{ExecutionContext, Future}

class SerializationTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override val initializeParams: AlsInitializeParams = AlsInitializeParams(
    Some(AlsClientCapabilities(serialization = Some(SerializationClientCapabilities(true)))),
    Some(TraceKind.Off))

  test("Parse Model and check serialized json ld notification") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
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
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(api, platform)
                .withEnvironment(env)
                .build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#1")
      }
    }
  }

  test("Request serialized model") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
      val content =
        """#%RAML 1.0
          |title: test
          |description: missing title
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)

      for {
        _ <- alsClient.nextCall.map(_.model.toString)
        s <- serialized(server, api, serializationProps)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(api, platform)
                .withEnvironment(env)
                .build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#1")
      }
    }
  }

  test("Request serialized model twice and change") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
      val content =
        """#%RAML 1.0
          |title: test
          |description: missing title
          |""".stripMargin

      val api = "file://api.raml"
      openFile(server)(api, content)

      for {
        _       <- alsClient.nextCall.map(_.model.toString)
        s       <- serialized(server, api, serializationProps)
        parsed  <- parsedApi(api, s)
        s2      <- serialized(server, api, serializationProps)
        parsed2 <- parsedApi(api, s2)
        s3 <- {
          changeFile(server)(api, "", 1)
          serialized(server, api, serializationProps)
        }
        parsed3 <- parsedApi(api, s3)
        s4 <- {
          changeFile(server)(api, content, 2)
          serialized(server, api, serializationProps)
        }
        parsed4 <- parsedApi(api, s4)
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#1")
        parsed2.asInstanceOf[Document].encodes.id should be("amf://id#1")
        parsed3.isInstanceOf[Document] should be(false)
        parsed4.asInstanceOf[Document].encodes.id should be("amf://id#1")

      }
    }
  }

  private def serialized(server: LanguageServer, api: String, serializationProps: SerializationProps[StringWriter]) = {
    server
      .resolveHandler(serializationProps.requestType)
      .value
      .apply(SerializationParams(TextDocumentIdentifier(api)))
      .map(_.model.toString)
  }

  private def parsedApi(api: String, s: String) = {

    val env = Environment(new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] =
        Future.successful(new Content(s, api))

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
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
      val url = filePath("raml-endpoint-sorting.raml")

      for {
        _ <- platform.resolve(url).map { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
        s <- serialized(server, url, serializationProps)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(url, platform)
                .withEnvironment(env)
                .build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#1")
      }
    }
  }

  test("two requests") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
      val url = filePath("raml-endpoint-sorting.raml")

      for {
        _ <- platform.resolve(url).map { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
        s <- serialized(server, url, serializationProps)
        parsed <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(url, platform)
                .withEnvironment(env)
                .build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
        s2 <- serialized(server, url, serializationProps)
        parsed2 <- {
          val env = Environment(new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s2, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          })
          RuntimeCompiler
            .forContext(
              new CompilerContextBuilder(url, platform)
                .withEnvironment(env)
                .build(),
              Some(Mimes.`APPLICATION/LD+JSONLD`),
              Some(Amf.name),
              UnspecifiedReference
            )
        }
      } yield {
        parsed.asInstanceOf[Document].encodes.id should be("amf://id#1")
        parsed2.asInstanceOf[Document].encodes.id should be("amf://id#1")
      }
    }
  }

  test("Files outside tree shouldn't overwrite main file cache") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
      val mainUrl      = filePath("project/librarybooks.raml")
      val extensionUrl = filePath("project/extension.raml")
      val overlayUrl   = filePath("project/overlay.raml")

      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("project")}")))
        _ <- platform
          .resolve(mainUrl)
          .map(c =>
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(mainUrl, "RAML", 0, c.stream.toString))))
        mainSerialized1 <- serialized(server, mainUrl, serializationProps)
        _ <- platform
          .resolve(extensionUrl)
          .map(c => {
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(extensionUrl, "RAML", 0, c.stream.toString)))
            server.textDocumentSyncConsumer.didFocus(DidFocusParams(extensionUrl, 0))
          })
        extensionSerialized <- serialized(server, extensionUrl, serializationProps)
        _                   <- Future(server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0)))
        mainSerialized2     <- serialized(server, mainUrl, serializationProps)
        _ <- platform
          .resolve(overlayUrl)
          .map(c => {
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(overlayUrl, "RAML", 0, c.stream.toString)))
            server.textDocumentSyncConsumer.didFocus(DidFocusParams(overlayUrl, 0))
          })
        overlaySerialized <- serialized(server, overlayUrl, serializationProps)
        _                 <- Future(server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0)))
        mainSerialized3   <- serialized(server, mainUrl, serializationProps)
      } yield {
        (mainSerialized1 == mainSerialized2) should be(true)
        (mainSerialized2 == mainSerialized3) should be(true)
        (extensionSerialized != mainSerialized1) should be(true)
        (extensionSerialized != overlaySerialized) should be(true)
        (overlaySerialized != mainSerialized1) should be(true)
      }
    }
  }

  test("Files outside tree shouldn't overwrite main file cache - overlay main file") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(): DocBuilder[StringWriter] =
          JsonOutputBuilder()
      }
    withServer(buildServer(alsClient, serializationProps)) { server =>
      val mainUrl      = filePath("project-overlay-mf/librarybooks.raml")
      val extensionUrl = filePath("project-overlay-mf/extension.raml")
      val overlayUrl   = filePath("project-overlay-mf/overlay.raml")

      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("project-overlay-mf")}")))
        _ <- platform
          .resolve(overlayUrl)
          .map(c => {
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(overlayUrl, "RAML", 0, c.stream.toString)))
            server.textDocumentSyncConsumer.didFocus(DidFocusParams(overlayUrl, 0))
          })
        overlaySerialized <- serialized(server, overlayUrl, serializationProps)
        _                 <- Future(server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0)))
        _ <- platform
          .resolve(mainUrl)
          .map(c =>
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(mainUrl, "RAML", 0, c.stream.toString))))
        mainSerialized1 <- serialized(server, mainUrl, serializationProps)
        _ <- platform
          .resolve(extensionUrl)
          .map(c => {
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(extensionUrl, "RAML", 0, c.stream.toString)))
            server.textDocumentSyncConsumer.didFocus(DidFocusParams(extensionUrl, 0))
          })
        extensionSerialized <- serialized(server, extensionUrl, serializationProps)
        _                   <- Future(server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0)))
        mainSerialized2     <- serialized(server, mainUrl, serializationProps)
        _                   <- Future(server.textDocumentSyncConsumer.didFocus(DidFocusParams(overlayUrl, 0)))
        overlaySerialized2  <- serialized(server, overlayUrl, serializationProps)
      } yield {
        Tests.checkDiff(mainSerialized2, mainSerialized1)
        val diffs = Diff.trimming.ignoreEmptyLines.diff(overlaySerialized, mainSerialized1)
        checkDiffsAreIrrelevant(diffs)
        Tests.checkDiff(overlaySerialized2, mainSerialized1)
        (extensionSerialized != overlaySerialized) should be(true)
      }
    }
  }

  /**
    * ALS-1378
    * Checks if differences in the serialization result are irrelevant. This differences are generated by the fact
    * that the API is parsed from different contexts, and in some cases adds or removes empty fields.
    * Even though the resulting serialization are different when looked at as literal strings,
    * the model still represents the same model if the only fields missing or being added are
    * empty, an thus should not fail.
    * @param diffs list of Diff.Delta containing the differences between the serializations
    */
  private def checkDiffsAreIrrelevant(diffs: List[Diff.Delta[String]]): Unit = {
    if (diffs.nonEmpty) {
      diffs.foreach(delta => {
        if (delta.t == Diff.Delete && delta.aLines.forall(line => {
              isEmptySequence(line)
            })) {
          // "doc:declares": [],
          logger.warning("Missing empty sequence in serialization", "SerializationTest", "overlay main file")
        } else {
          logger.debug("Non-empty sequence in serialization", "SerializationTest", "overlay main file")
          fail(s"\n ${delta.t}" + makeString(diffs))
        }
      })
    }
  }

  private def isEmptySequence(line: String): Boolean = {
    YamlParser(line.replace(",", ""))(IgnoringErrorHandler())
      .parse(false)
      .collectFirst({ case d: YDocument => d })
      .map(_.as[YMap].entries.head.value.value)
      .exists({
        case seq: YSequence => seq.isEmpty
        case _              => false
      })
  }

  def buildServer(alsClient: MockAlsClientNotifier,
                  serializationProps: SerializationProps[StringWriter]): LanguageServer = {
    val factoryBuilder: WorkspaceManagerFactoryBuilder =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger)
    val serializationManager: SerializationManager[StringWriter] =
      factoryBuilder.serializationManager(serializationProps)
    val factory: WorkspaceManagerFactory =
      factoryBuilder.buildWorkspaceManagerFactory()
    val builder =
      new LanguageServerBuilder(factory.documentManager,
                                factory.workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
    builder.addInitializableModule(serializationManager)
    builder.addRequestModule(serializationManager)
    builder.build()
  }

  override def rootPath: String = "serialization"
}
