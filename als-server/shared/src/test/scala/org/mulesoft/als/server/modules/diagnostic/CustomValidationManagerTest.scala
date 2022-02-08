package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.AMFGraphConfiguration
import amf.custom.validation.client.scala.BaseProfileValidatorBuilder
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.feature.diagnostic.CustomValidationClientCapabilities
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticImplicits.PublishDiagnosticsParamsWriter
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.workspace.ChangesWorkspaceConfiguration
import org.mulesoft.als.server.{
  LanguageServerBaseTest,
  MockAlsClientNotifier,
  MockDiagnosticClientNotifier,
  SerializationProps
}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.feature.common.{Location, Position, Range}
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticSeverity, PublishDiagnosticsParams}
import org.yaml.builder.{DocBuilder, JsonOutputBuilder}

import java.io.StringWriter
import scala.concurrent.{ExecutionContext, Future}

class CustomValidationManagerTest
    extends LanguageServerBaseTest
    with ChangesWorkspaceConfiguration
    with FileAssertionTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "diagnostics"

  val workspacePath: String         = filePath(platform.encodeURI("project"))
  val mainFileName: String          = "api.raml"
  val mainFile: String              = filePath(platform.encodeURI("project/api.raml"))
  val serializedUri: String         = filePath(platform.encodeURI("project/api.raml.jsonld"))
  val isolatedFile: String          = filePath(platform.encodeURI("project/isolated.raml"))
  val serializedIsolatedUri: String = filePath(platform.encodeURI("project/isolated.raml.jsonld"))
  val profileUri: String            = filePath(platform.encodeURI("project/profile.yaml"))

  def buildInitParams(workspacePath: String): AlsInitializeParams =
    buildInitParams(Some(workspacePath))

  def buildInitParams(workspacePath: Option[String] = None): AlsInitializeParams =
    AlsInitializeParams(
      Some(AlsClientCapabilities(customValidations = Some(CustomValidationClientCapabilities(true)))),
      Some(TraceKind.Off),
      rootUri = workspacePath
    )

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier,
                  validator: BaseProfileValidatorBuilder): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, EditorConfiguration())
    val dm      = builder.buildDiagnosticManagers(Some(validator))
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  def buildServerForSerialize(diagnosticNotifier: MockDiagnosticClientNotifier,
                              validator: BaseProfileValidatorBuilder,
                              s: SerializationProps[_]): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, EditorConfiguration())
    val dm      = builder.buildDiagnosticManagers(Some(validator))
    val sm      = builder.serializationManager(s)
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    b.addInitializableModule(sm)
    b.addRequestModule(sm)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  def getDiagnostics(implicit diagnosticNotifier: MockDiagnosticClientNotifier): Future[PublishDiagnosticsParams] = {
    for {
      _           <- diagnosticNotifier.nextCall // resolution diagnostics
      diagnostics <- diagnosticNotifier.nextCall // custom validation diagnostics
    } yield diagnostics
  }

  ignore("Should not be called when no profile is registered") {
    implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val validator                                                 = FromJsonLdValidatorProvider.empty
    val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
    val initialArgs                                               = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set.empty)
    withServer(server, buildInitParams(workspacePath)) { server =>
      for {
        content <- platform.fetchContent(mainFile, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _       <- changeWorkspaceConfiguration(server)(initialArgs)
        _       <- getDiagnostics
        _       <- changeNotification(server)(mainFile, content.replace("Not found", "Not found!"), 1)
        _       <- getDiagnostics
        _       <- validator.jsonLDValidatorExecutor.calledNTimes(0)
      } yield succeed
    }
  }

  ignore("Shouldn't even run when disabled") {
    implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val validator                                                 = FromJsonLdValidatorProvider.empty
    val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
    val initialArgs                                               = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set.empty)
    withServer(server, AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(workspacePath))) { _ =>
      for {
        _       <- changeWorkspaceConfiguration(server)(initialArgs)
        content <- platform.fetchContent(mainFile, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _       <- diagnosticNotifier.nextCall // resolution diagnostics
        _       <- changeNotification(server)(mainFile, content.replace("Not found", "Not found!"), 1)
        _       <- diagnosticNotifier.nextCall // resolution diagnostics
        _       <- validator.jsonLDValidatorExecutor.calledNTimes(0)
      } yield {
        diagnosticNotifier.promises should be(empty)
      }
    }
  }

  ignore("Should be called after a profile is registered") {
    implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val validator                                                 = FromJsonLdValidatorProvider.empty
    val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
    val initialArgs                                               = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set.empty)
    val args                                                      = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))

    withServer(server, buildInitParams(workspacePath)) { _ =>
      for {
        _ <- changeWorkspaceConfiguration(server)(initialArgs)
        _ <- validator.jsonLDValidatorExecutor.calledNTimes(0)
        _ <- getDiagnostics
        _ <- changeWorkspaceConfiguration(server)(args) // register
        _ <- validator.jsonLDValidatorExecutor.calledNTimes(1)
        _ <- getDiagnostics
        _ <- changeWorkspaceConfiguration(server)(changeConfigArgs(Some(mainFileName), workspacePath)) // unregister
        _ <- getDiagnostics
      } yield {
        succeed
      }
    }
  }

  ignore("Request serialization profile") {
    implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
    val alsClient: MockAlsClientNotifier                          = new MockAlsClientNotifier

    val serializationProps: SerializationProps[StringWriter] =
      new SerializationProps[StringWriter](alsClient) {
        override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
          JsonOutputBuilder(prettyPrint)
      }

    val validator              = FromJsonLdValidatorProvider.empty
    val server: LanguageServer = buildServerForSerialize(diagnosticNotifier, validator, serializationProps)
    val args                   = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))

    withServer(server, buildInitParams(workspacePath)) { _ =>
      for {
        _ <- changeWorkspaceConfiguration(server)(args)
        r <- serialize(server, profileUri, serializationProps)
      } yield {
        r.contains("Test profile 1") shouldBe true
        r.contains("meta:DialectInstance") shouldBe true
      }
    }
  }

  ignore("Should notify errors on main tree") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val args                                                      = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))

        withServer(server, buildInitParams(workspacePath)) {
          _ =>
            for {
              _           <- changeWorkspaceConfiguration(server)(args) // register
              profile     <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- validator.jsonLDValidatorExecutor.calledNTimes(1)
              diagnostics <- getDiagnostics
              _           <- validator.jsonLDValidatorExecutor.called(profile, serializedUri)
            } yield {
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

  ignore("Should notify errors on isolated files") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val args                                                      = changeConfigArgs(None, workspacePath, Set.empty, Set(profileUri))

        withServer(server, buildInitParams(workspacePath)) {
          server =>
            for {
              content     <- platform.fetchContent(isolatedFile, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- openFile(server)(isolatedFile, content)
              _           <- getDiagnostics
              _           <- changeWorkspaceConfiguration(server)(args) // register
              profile     <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- validator.jsonLDValidatorExecutor.calledNTimes(1)
              diagnostics <- getDiagnostics
              _           <- validator.jsonLDValidatorExecutor.called(profile, serializedIsolatedUri)
            } yield {
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

  ignore("Should build traces") {
    val negativeReportUri = filePath(platform.encodeURI("traces.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val args                                                      = changeConfigArgs(None, workspacePath, Set.empty, Set(profileUri))

        withServer(server, buildInitParams(workspacePath)) {
          server =>
            for {
              content     <- platform.fetchContent(isolatedFile, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- openFile(server)(isolatedFile, content)
              _           <- getDiagnostics
              _           <- changeWorkspaceConfiguration(server)(args) // register
              profile     <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- validator.jsonLDValidatorExecutor.calledNTimes(1)
              diagnostics <- getDiagnostics
              _           <- validator.jsonLDValidatorExecutor.called(profile, serializedIsolatedUri)
            } yield {
              val firstDiagnostic = diagnostics.diagnostics.find(
                _.message == "Min length must be less than max length must match in scalar")
              if (firstDiagnostic.isEmpty) {
                logger.error(s"Couldn't find first diagnostic:\n ${diagnostics.write}",
                             "CustomValidationManagerTest",
                             "Should notify errors")
                fail("Couldn't find first diagnostic")
              }
              val diagnostic = firstDiagnostic.get
              diagnostic.range should be(Range(Position(7, 4), Position(12, 0)))
              diagnostic.severity should equal(Some(DiagnosticSeverity.Error))

              val related = diagnostic.relatedInformation.get.headOption.get
              related.location should equal(
                Location("file://als-server/shared/src/test/resources/diagnostics/project/isolated.raml",
                         Range(Position(7, 4), Position(12, 0))))
              related.message should equal("Error expected 500 < actual (actual=100) at shacl.minLength")
            }
        }
      })
  }

  ignore("Should notify errors on both the main tree and isolated files") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val initialArgs                                               = changeConfigArgs(Some(mainFileName), workspacePath)
        val args                                                      = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))

        withServer(server, buildInitParams(workspacePath)) {
          server =>
            for {
              _       <- changeWorkspaceConfiguration(server)(initialArgs) // Set main file
              content <- platform.fetchContent(isolatedFile, AMFGraphConfiguration.predefined()).map(_.toString())
              _       <- openFile(server)(isolatedFile, content)
              _       <- getDiagnostics // main file
              _       <- getDiagnostics // isolated file
              _       <- changeWorkspaceConfiguration(server)(args) // register
              _       <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _       <- validator.jsonLDValidatorExecutor.calledNTimes(2)
              d1      <- diagnosticNotifier.nextCall // resolution diagnostics main file
              d2      <- diagnosticNotifier.nextCall // resolution diagnostics isolated file
              d3      <- diagnosticNotifier.nextCall // custom validation diagnostics main file
              d4      <- diagnosticNotifier.nextCall // custom validation diagnostics isolated file
            } yield {
              val validatedUris = Seq(d1, d2, d3, d4).map(_.uri)
              validatedUris should contain(isolatedFile)
              validatedUris should contain(mainFile)
              validatedUris.count(_ == isolatedFile) should equal(2)
              validatedUris.count(_ == mainFile) should equal(2)
            }
        }
      })
  }

  ignore("Should notify errors on isolated files if opened after profile is added") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val args                                                      = changeConfigArgs(None, workspacePath, Set.empty, Set(profileUri))

        withServer(server, buildInitParams(workspacePath)) {
          server =>
            for {
              _           <- changeWorkspaceConfiguration(server)(args) // register
              content     <- platform.fetchContent(isolatedFile, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- openFile(server)(isolatedFile, content)
              profile     <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _           <- validator.jsonLDValidatorExecutor.calledNTimes(1)
              diagnostics <- getDiagnostics
              _           <- validator.jsonLDValidatorExecutor.called(profile, serializedIsolatedUri)
            } yield {
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

  ignore("Should clean custom-validation errors if profile is removed") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val args                                                      = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))
        val args2                                                     = changeConfigArgs(Some(mainFileName), workspacePath)

        withServer(server, buildInitParams(workspacePath)) {
          _ =>
            for {
              _               <- changeWorkspaceConfiguration(server)(args2) // set mainFile
              _               <- getDiagnostics // initial parse
              _               <- changeWorkspaceConfiguration(server)(args) // register
              profile         <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _               <- validator.jsonLDValidatorExecutor.calledNTimes(1)
              diagnostics     <- getDiagnostics
              _               <- validator.jsonLDValidatorExecutor.called(profile, serializedUri)
              _               <- changeWorkspaceConfiguration(server)(args2) // unregister
              cleanDiagnostic <- getDiagnostics
            } yield {
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

  ignore("Should clean custom-validation errors if profile is removed [Isolated files]") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        implicit val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(3000)
        val validator                                                 = FromJsonLdValidatorProvider(negativeReport.toString)
        val server: LanguageServer                                    = buildServer(diagnosticNotifier, validator)
        val args                                                      = changeConfigArgs(None, workspacePath, Set.empty, Set(profileUri))
        val args2                                                     = changeConfigArgs(None, workspacePath)

        withServer(server, buildInitParams(workspacePath)) {
          server =>
            for {
              content         <- platform.fetchContent(isolatedFile, AMFGraphConfiguration.predefined()).map(_.toString())
              _               <- openFile(server)(isolatedFile, content)
              _               <- getDiagnostics // Open file
              _               <- changeWorkspaceConfiguration(server)(args) // register
              _               <- validator.jsonLDValidatorExecutor.calledNTimes(1)
              diagnostics     <- getDiagnostics
              profile         <- platform.fetchContent(profileUri, AMFGraphConfiguration.predefined()).map(_.toString())
              _               <- validator.jsonLDValidatorExecutor.called(profile, serializedIsolatedUri)
              _               <- changeWorkspaceConfiguration(server)(args2) // unregister
              cleanDiagnostic <- getDiagnostics
            } yield {
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
