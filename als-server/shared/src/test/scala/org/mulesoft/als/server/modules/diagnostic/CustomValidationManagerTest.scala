package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.configuration.ConfigurationStyle.COMMAND
import org.mulesoft.als.configuration.ProjectConfigurationStyle
import org.mulesoft.als.server.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.{ChangesWorkspaceConfiguration, WorkspaceManager}
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{Position, Range}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity
import org.scalatest.Assertion
import DiagnosticImplicits.PublishDiagnosticsParamsWriter
import scala.concurrent.{ExecutionContext, Future}

class CustomValidationManagerTest
    extends LanguageServerBaseTest
    with ChangesWorkspaceConfiguration
    with FileAssertionTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "custom-diagnostics"

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier,
                  validator: AMFOpaValidator): (LanguageServer, WorkspaceManager) = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    val dm      = builder.buildDiagnosticManagers(Some(validator))
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    dm.foreach(b.addInitializableModule)
    (b.build(), factory.workspaceManager)
  }

  class DummyAmfOpaValidator(val result: String = "{}") extends AMFOpaValidator {
    override val logger: Logger = EmptyLogger

    private var calls: Map[String, String] = Map.empty
    private var callCount: Int             = 0

    override def validateWithProfile(profile: String, data: String): Future[ValidationResult] = {
      calls = calls + (profile -> data)
      callCount = callCount + 1
      Future.successful(result)
    }

    def called(profile: String, goldenUri: String): Future[Assertion] = {
      val v = calls.get(profile)
      assert(v.isDefined)
      for {
        tmp <- writeTemporaryFile(goldenUri)(v.get)
        r   <- assertDifferences(tmp, goldenUri)
      } yield r
    }

    def calledNTimes(n: Int): Assertion = assert(callCount == n)
  }

  test("Should not be called when no profile is registered") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val validator                                        = new DummyAmfOpaValidator
    val workspacePath                                    = filePath(platform.encodeURI("project"))
    val mainFile                                         = filePath(platform.encodeURI("project/api.raml"))
    val (server, workspaceManager)                       = buildServer(diagnosticNotifier, validator)
    withServer(server) { server =>
      for {
        content <- platform.fetchContent(mainFile, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _       <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(workspacePath)))
        _       <- diagnosticNotifier.nextCall
        _       <- diagnosticNotifier.nextCall
        _       <- changeNotification(server)(mainFile, content.replace("Not found", "Not found!"), 1)
        _       <- diagnosticNotifier.nextCall
        _       <- diagnosticNotifier.nextCall
      } yield {
        validator.calledNTimes(0)
      }
    }
  }

  test("Should be called after a profile is registered") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val validator                                        = new DummyAmfOpaValidator
    val workspacePath                                    = filePath(platform.encodeURI("project"))
    val mainFile                                         = filePath(platform.encodeURI("project/api.raml"))
    val profile                                          = filePath(platform.encodeURI("project/profile.yaml"))
    val (server, workspaceManager)                       = buildServer(diagnosticNotifier, validator)
    val args                                             = wrapJson(mainFile, None, Set.empty, Set(profile))

    withServer(server) { server =>
      for {
        _ <- server.initialize(
          AlsInitializeParams(None,
                              Some(TraceKind.Off),
                              rootUri = Some(workspacePath),
                              projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
        _ <- changeWorkspaceConfiguration(
          workspaceManager,
          s"""{"mainUri": "$mainFile", "dependencies": [], "customValidationProfiles": []}""") // initial configuration
        _ <- diagnosticNotifier.nextCall // api.raml (Resolution)
        _ <- diagnosticNotifier.nextCall // api.raml (CustomValidations)
        _ <- Future { validator.calledNTimes(0) }
        _ <- changeWorkspaceConfiguration(workspaceManager, args) // register
        _ <- diagnosticNotifier.nextCall // Resolution diagnostic manager
        _ <- diagnosticNotifier.nextCall // Custom validation manager
        _ <- Future { validator.calledNTimes(1) }
        _ <- changeWorkspaceConfiguration(workspaceManager, wrapJson(mainFile)) // unregister
        _ <- diagnosticNotifier.nextCall // Resolution diagnostic manager
        _ <- diagnosticNotifier.nextCall // Custom diagnostic manager
      } yield {
        validator.calledNTimes(1)
      }
    }
  }

  test("Should notify errors") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(13000)
        val validator                                        = new DummyAmfOpaValidator(negativeReport.toString)
        val workspacePath                                    = filePath(platform.encodeURI("project"))
        val mainFile                                         = filePath(platform.encodeURI("project/api.raml"))
        val serializedUri                                    = filePath(platform.encodeURI("project/api.raml.jsonld"))
        val profileUri                                       = filePath(platform.encodeURI("project/profile.yaml"))
        val (server, workspaceManager)                       = buildServer(diagnosticNotifier, validator)
        val args                                             = wrapJson(mainFile, None, Set.empty, Set(profileUri))

        withServer(server) {
          server =>
            for {
              _ <- server.initialize(
                AlsInitializeParams(None,
                                    Some(TraceKind.Off),
                                    rootUri = Some(workspacePath),
                                    projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
              _           <- changeWorkspaceConfiguration(workspaceManager, args) // register
              profile     <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- diagnosticNotifier.nextCall // Resolution diagnostic manager
              diagnostics <- diagnosticNotifier.nextCall // Custom validation manager
              _           <- validator.called(profile, serializedUri)
            } yield {
              validator.calledNTimes(1)
              val firstDiagnostic =
                diagnostics.diagnostics.find(d => d.range.start == Position(5, 6) && d.range.end == Position(6, 19))
              if (firstDiagnostic.isEmpty) {
                logger.error(s"Couldn't find first diagnostic:\n ${diagnostics.write}",
                             "CustomValidationManagerTest",
                             "Should notify errors")
                fail("Couldn't find first diagnostic")
              }
              val diagnostic = firstDiagnostic.get
              diagnostic.message should be("Scalars in parameters must have minLength defined")
              diagnostic.range should be(Range(Position(5, 6), Position(6, 19)))
              diagnostic.severity should equal(Some(DiagnosticSeverity.Error))
            }
        }
      })
  }

  test("Should clean custom-validation errors if profile is removed") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(13000)
        val validator                                        = new DummyAmfOpaValidator(negativeReport.toString)
        val workspacePath                                    = filePath(platform.encodeURI("project"))
        val mainFile                                         = filePath(platform.encodeURI("project/api.raml"))
        val serializedUri                                    = filePath(platform.encodeURI("project/api.raml.jsonld"))
        val profileUri                                       = filePath(platform.encodeURI("project/profile.yaml"))
        val (server, workspaceManager)                       = buildServer(diagnosticNotifier, validator)
        val args                                             = wrapJson(mainFile, None, Set.empty, Set(profileUri))
        val args2                                            = wrapJson(mainFile, None)

        withServer(server) {
          server =>
            for {
              _ <- server.initialize(
                AlsInitializeParams(None,
                                    Some(TraceKind.Off),
                                    rootUri = Some(workspacePath),
                                    projectConfigurationStyle = Some(ProjectConfigurationStyle(COMMAND))))
              _               <- changeWorkspaceConfiguration(workspaceManager, args) // register
              profile         <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _               <- diagnosticNotifier.nextCall // Resolution diagnostic manager
              diagnostics     <- diagnosticNotifier.nextCall // Custom validation manager
              _               <- validator.called(profile, serializedUri)
              _               <- changeWorkspaceConfiguration(workspaceManager, args2) // unregister
              _               <- diagnosticNotifier.nextCall // Resolution diagnostic manager
              cleanDiagnostic <- diagnosticNotifier.nextCall // Custom validation manager
            } yield {
              validator.calledNTimes(1)
              val firstDiagnostic =
                diagnostics.diagnostics.find(d => d.range.start == Position(5, 6) && d.range.end == Position(6, 19))
              if (firstDiagnostic.isEmpty) {
                logger.error(s"Couldn't find first diagnostic:\n ${diagnostics.write}",
                             "CustomValidationManagerTest",
                             "Should notify errors")
                fail("Couldn't find first diagnostic")
              }
              val diagnostic = firstDiagnostic.get
              diagnostic.message should be("Scalars in parameters must have minLength defined")
              diagnostic.range should be(Range(Position(5, 6), Position(6, 19)))
              cleanDiagnostic.diagnostics
                .filter(_.message == "Scalars in parameters must have minLength defined") should be(empty)
            }
        }
      })
  }

}
