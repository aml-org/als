package org.mulesoft.als.server.modules.serialization

import amf.apicontract.client.scala.WebAPIConfiguration
import amf.core.client.common.remote.Content
import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.IgnoringErrorHandler
import amf.core.client.scala.model.document.Document
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.common.diff.Diff.makeString
import org.mulesoft.als.common.diff.{Diff, FileAssertionTest, Tests}
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
import amf.core.internal.plugins.syntax.SyamlAMFErrorHandler
import org.mulesoft.als.configuration.ConfigurationStyle.COMMAND
import org.mulesoft.als.configuration.ProjectConfigurationStyle
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.diagnostic.DummyAmfOpaValidator
import org.mulesoft.als.server.workspace.{ChangesWorkspaceConfiguration, WorkspaceManager}
import org.scalatest.Assertion

import java.io.StringWriter
import scala.concurrent.{ExecutionContext, Future}

class SerializationTest extends LanguageServerBaseTest with ChangesWorkspaceConfiguration with FileAssertionTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override val initializeParams: AlsInitializeParams = AlsInitializeParams(
    Some(AlsClientCapabilities(serialization = Some(SerializationClientCapabilities(true)))),
    Some(TraceKind.Off))

  test("Parse Model and check serialized json ld notification") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
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
          val rl = new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          }
          WebAPIConfiguration
            .WebAPI()
            .withResourceLoader(rl)
            .baseUnitClient()
            .parse(api)
        }
      } yield {
        parsed.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
      }
    }
  }

  test("Request serialized model") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
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
          val rl = new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, api))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == api
          }
          WebAPIConfiguration
            .WebAPI()
            .withResourceLoader(rl)
            .baseUnitClient()
            .parse(api)
        }
      } yield {
        parsed.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
      }
    }
  }

  test("Request serialized model twice and change") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
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
        _       <- changeFile(server)(api, "", 1)
        s3      <- serialized(server, api, serializationProps)
        parsed3 <- parsedApi(api, s3)
        _       <- changeFile(server)(api, content, 2)
        s4      <- serialized(server, api, serializationProps)
        parsed4 <- parsedApi(api, s4)
      } yield {
        parsed.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
        parsed2.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
        parsed3.baseUnit.isInstanceOf[Document] should be(false)
        parsed4.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")

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

    val rl = new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] =
        Future.successful(new Content(s, api))

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean = resource == api
    }
    WebAPIConfiguration
      .WebAPI()
      .withResourceLoader(rl)
      .baseUnitClient()
      .parse(api)

  }

  test("basic test") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
      val url = filePath("raml-endpoint-sorting.raml")

      for {
        _ <- platform.fetchContent(url, AMFGraphConfiguration.predefined()).flatMap { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
        s <- serialized(server, url, serializationProps)
        parsed <- {
          val rl = new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          }
          WebAPIConfiguration
            .WebAPI()
            .withResourceLoader(rl)
            .baseUnitClient()
            .parse(url)
        }
      } yield {
        parsed.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
      }
    }
  }

  test("two requests") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
      val url = filePath("raml-endpoint-sorting.raml")

      for {
        _ <- platform.fetchContent(url, AMFGraphConfiguration.predefined()).map { c =>
          server.textDocumentSyncConsumer.didOpen(DidOpenTextDocumentParams(
            TextDocumentItem(url, "RAML", 0, c.stream.toString))) // why clean empty lines was necessary?
        }
        s <- serialized(server, url, serializationProps)
        parsed <- {
          val rl = new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          }
          WebAPIConfiguration
            .WebAPI()
            .withResourceLoader(rl)
            .baseUnitClient()
            .parse(url)
        }
        s2 <- serialized(server, url, serializationProps)
        parsed2 <- {
          val rl = new ResourceLoader {

            /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
            override def fetch(resource: String): Future[Content] =
              Future.successful(new Content(s, url))

            /** Accepts specified resource. */
            override def accepts(resource: String): Boolean = resource == url
          }
          WebAPIConfiguration
            .WebAPI()
            .withResourceLoader(rl)
            .baseUnitClient()
            .parse(url)
        }
      } yield {
        parsed.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
        parsed2.baseUnit.asInstanceOf[Document].encodes.id should be("amf://id#2")
      }
    }
  }

  test("Files outside tree shouldn't overwrite main file cache") {
    val alsClient: MockAlsClientNotifier = new MockAlsClientNotifier
    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
      val mainUrl      = filePath("project/librarybooks.raml")
      val extensionUrl = filePath("project/extension.raml")
      val overlayUrl   = filePath("project/overlay.raml")

      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("project")}")))
        _ <- platform
          .fetchContent(mainUrl, AMFGraphConfiguration.predefined())
          .flatMap(c =>
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(mainUrl, "RAML", 0, c.stream.toString))))
        mainSerialized1 <- serialized(server, mainUrl, serializationProps)
        _ <- platform
          .fetchContent(extensionUrl, AMFGraphConfiguration.predefined())
          .flatMap(c => {
            server.textDocumentSyncConsumer
              .didOpen(DidOpenTextDocumentParams(TextDocumentItem(extensionUrl, "RAML", 0, c.stream.toString)))
              .flatMap(_ => server.textDocumentSyncConsumer.didFocus(DidFocusParams(extensionUrl, 0)))
          })
        extensionSerialized <- serialized(server, extensionUrl, serializationProps)
        _                   <- server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0))
        mainSerialized2     <- serialized(server, mainUrl, serializationProps)
        _ <- platform
          .fetchContent(overlayUrl, AMFGraphConfiguration.predefined())
          .flatMap(c => {
            server.textDocumentSyncConsumer
              .didOpen(DidOpenTextDocumentParams(TextDocumentItem(overlayUrl, "RAML", 0, c.stream.toString)))
              .flatMap(_ => server.textDocumentSyncConsumer.didFocus(DidFocusParams(overlayUrl, 0)))
          })
        overlaySerialized <- serialized(server, overlayUrl, serializationProps)
        _                 <- server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0))
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
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    withServer(buildServer(serializationProps)) { server =>
      val mainUrl      = filePath("project-overlay-mf/librarybooks.raml")
      val extensionUrl = filePath("project-overlay-mf/extension.raml")
      val overlayUrl   = filePath("project-overlay-mf/overlay.raml")

      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("project-overlay-mf")}")))
        _ <- platform
          .fetchContent(overlayUrl, AMFGraphConfiguration.predefined())
          .flatMap(c => {
            server.textDocumentSyncConsumer
              .didOpen(DidOpenTextDocumentParams(TextDocumentItem(overlayUrl, "RAML", 0, c.stream.toString)))
              .flatMap(_ => server.textDocumentSyncConsumer.didFocus(DidFocusParams(overlayUrl, 0)))
          })
        overlaySerialized <- serialized(server, overlayUrl, serializationProps)
        _                 <- Future(server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0)))
        _ <- platform
          .fetchContent(mainUrl, AMFGraphConfiguration.predefined())
          .flatMap(c =>
            server.textDocumentSyncConsumer.didOpen(
              DidOpenTextDocumentParams(TextDocumentItem(mainUrl, "RAML", 0, c.stream.toString))))
        mainSerialized1 <- serialized(server, mainUrl, serializationProps)
        _ <- platform
          .fetchContent(extensionUrl, AMFGraphConfiguration.predefined())
          .flatMap(c => {
            server.textDocumentSyncConsumer
              .didOpen(DidOpenTextDocumentParams(TextDocumentItem(extensionUrl, "RAML", 0, c.stream.toString)))
              .flatMap(_ => server.textDocumentSyncConsumer.didFocus(DidFocusParams(extensionUrl, 0)))
          })
        extensionSerialized <- serialized(server, extensionUrl, serializationProps)
        _                   <- server.textDocumentSyncConsumer.didFocus(DidFocusParams(mainUrl, 0))
        mainSerialized2     <- serialized(server, mainUrl, serializationProps)
        _                   <- server.textDocumentSyncConsumer.didFocus(DidFocusParams(overlayUrl, 0))
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
    val syamlEH = new SyamlAMFErrorHandler(IgnoringErrorHandler)
    YamlParser(line.replace(",", ""))(syamlEH)
      .parse(false)
      .collectFirst({ case d: YDocument => d })
      .map(_.as[YMap].entries.head.value.value)
      .exists({
        case seq: YSequence => seq.isEmpty
        case _              => false
      })
  }

  val mainUrl: String         = filePath("custom-validation/api.raml")
  val goldenUrl: String       = filePath("custom-validation/profile-serialized-golden.jsonld")
  val editedGoldenUrl: String = filePath("custom-validation/profile-serialized-edited-golden.jsonld")
  val profileUrl: String      = filePath("custom-validation/profile.yaml")
  val workspace               = s"${filePath("custom-validation")}"

  test("Serialize registered validation profile") {
    val alsClient: MockAlsClientNotifier       = new MockAlsClientNotifier
    val notifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val initialArgs: String                    = changeConfigArgs(Some(mainUrl), Some(workspace), Set.empty, Set(profileUrl))
    implicit val serializationProps: SerializationProps[StringWriter] = {
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    }
    val (server, workspaceManager) =
      buildServerWithWorkspaceManager(serializationProps, notifier, withDiagnostics = true)
    withServer(server) { server =>
      for {
        _ <- server.initialize(
          AlsInitializeParams(None,
                              Some(TraceKind.Off),
                              rootUri = Some(workspace),
                              projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
        _ <- changeWorkspaceConfiguration(workspaceManager, initialArgs)
        _ <- notifier.nextCall
        r <- assertSerialization(server, profileUrl, goldenUrl)
      } yield r
    }
  }

  test("Serialize unregistered validation profile") {
    val workspace                              = s"${filePath("custom-validation")}"
    val alsClient: MockAlsClientNotifier       = new MockAlsClientNotifier
    val notifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    implicit val serializationProps: SerializationProps[StringWriter] = {
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    }
    val (server, _) = buildServerWithWorkspaceManager(serializationProps, notifier, withDiagnostics = true)
    withServer(server) { server =>
      for {
        _       <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(workspace)))
        content <- platform.fetchContent(profileUrl, AMFGraphConfiguration.predefined())
        _       <- openFile(server)(profileUrl, content.stream.toString)
        _       <- notifier.nextCall
        r       <- assertSerialization(server, profileUrl, goldenUrl)
      } yield r
    }
  }

  test("Serialize validation profile (editing workflow)") {
    val workspace                              = s"${filePath("custom-validation")}"
    val alsClient: MockAlsClientNotifier       = new MockAlsClientNotifier
    val notifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val initialArgs: String                    = changeConfigArgs(Some(mainUrl), Some(workspace), Set.empty, Set(profileUrl))
    implicit val serializationProps: SerializationProps[StringWriter] = {
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }
    }
    val (server, workspaceManager) =
      buildServerWithWorkspaceManager(serializationProps, notifier, withDiagnostics = true)
    withServer(server) { server =>
      for {
        _ <- server.initialize(
          AlsInitializeParams(None,
                              Some(TraceKind.Off),
                              rootUri = Some(workspace),
                              projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
        _       <- changeWorkspaceConfiguration(workspaceManager, initialArgs)
        _       <- notifier.nextCall
        _       <- assertSerialization(server, profileUrl, goldenUrl) // Registered profile
        content <- platform.fetchContent(profileUrl, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _       <- openFile(server)(profileUrl, content)
        _       <- notifier.nextCall
        _       <- assertSerialization(server, profileUrl, goldenUrl) // Opened profile
        _       <- changeFile(server)(profileUrl, content.replace("warning:\n  - ab", ""), 1)
        _       <- notifier.nextCall
        _       <- assertSerialization(server, profileUrl, editedGoldenUrl) // Edited profile
        _       <- closeFile(server)(profileUrl)
        r       <- assertSerialization(server, profileUrl, goldenUrl) // Closed profile (back to the registered one)
      } yield r
    }
  }

  def assertSerialization(server: LanguageServer, url: String, golden: String)(
      implicit serializationProps: SerializationProps[StringWriter]): Future[Assertion] =
    for {
      serialized <- serialized(server, url, serializationProps)
      tmp        <- writeTemporaryFile(golden)(serialized)
      r          <- assertDifferences(tmp, golden)
    } yield r

  def buildServer(serializationProps: SerializationProps[StringWriter],
                  notifier: Option[ClientNotifier] = None): LanguageServer =
    buildServerWithWorkspaceManager(serializationProps, notifier.getOrElse(new MockDiagnosticClientNotifier))._1

  def buildServerWithWorkspaceManager(serializationProps: SerializationProps[StringWriter],
                                      notifier: ClientNotifier,
                                      withDiagnostics: Boolean = false): (LanguageServer, WorkspaceManager) = {

    val factoryBuilder: WorkspaceManagerFactoryBuilder = new WorkspaceManagerFactoryBuilder(notifier, logger)
    val dm                                             = factoryBuilder.buildDiagnosticManagers(Some(new DummyAmfOpaValidator))
    val serializationManager: SerializationManager[StringWriter] =
      factoryBuilder.serializationManager(serializationProps)
    val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

    val builder =
      new LanguageServerBuilder(factory.documentManager,
                                factory.workspaceManager,
                                factory.configurationManager,
                                factory.resolutionTaskManager)
    builder.addInitializableModule(serializationManager)
    if (withDiagnostics) dm.foreach(m => builder.addInitializableModule(m))
    builder.addRequestModule(serializationManager)
    (builder.build(), factory.workspaceManager)
  }

  override def rootPath: String = "serialization"
}
