package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.AMFGraphConfiguration
import amf.validation.client.ProfileValidatorNodeBuilder
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.server.MockDiagnosticClientNotifier
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.feature.diagnostic.CustomValidationClientCapabilities
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.DiagnosticImplicits.PublishDiagnosticsParamsWriter
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.workspace.ChangesWorkspaceConfiguration
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.yaml.model.YDocument
import org.yaml.render.YamlRender

import scala.concurrent.{ExecutionContext, Future}

class NodeJsCustomValidationTest
    extends NodeJsLanguageServerBaseTest
    with ChangesWorkspaceConfiguration
    with FileAssertionTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "custom-validation"

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger, EditorConfiguration())
    val dm      = builder.buildDiagnosticManagers(Some(ProfileValidatorNodeBuilder))
    val factory = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  def buildInitArgs(workspaceFolder: String): AlsInitializeParams =
    AlsInitializeParams(
      Some(AlsClientCapabilities(customValidations = Some(CustomValidationClientCapabilities(true)))),
      Some(TraceKind.Off),
      rootUri = Some(workspaceFolder)
    )

  test("Should validate simple with simple profile") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(4000)
    val workspacePath                                    = filePath(platform.encodeURI("simple"))
    val mainFileName                                     = "api.raml"
    val mainFileUri                                      = filePath(platform.encodeURI("simple/api.raml"))
    val profile                                          = filePath(platform.encodeURI("simple/profile.yaml"))
    val expected                                         = filePath(platform.encodeURI("simple/expected/simple.yaml"))
    val args                                             = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profile))
    val server: LanguageServer                           = buildServer(diagnosticNotifier)
    withServer(server, buildInitArgs(workspacePath)) { server =>
      for {
        content     <- platform.fetchContent(mainFileUri, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _           <- openFile(server)(mainFileUri, content)
        _           <- diagnosticNotifier.nextCall
        _           <- diagnosticNotifier.nextCall
        _           <- changeWorkspaceConfiguration(server)(args)
        _           <- diagnosticNotifier.nextCall // resolution diagnostics
        diagnostics <- diagnosticNotifier.nextCall // custom validation diagnostics
        tmp         <- writeTemporaryFile(expected)(diagnostics.write)
        r           <- assertDifferences(tmp, expected)
      } yield r
    }
  }

  test("Should trigger validation after did change") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(4000)
    val workspacePath                                    = filePath(platform.encodeURI("simple"))
    val mainFileName                                     = "api.raml"
    val mainFileUri                                      = filePath(platform.encodeURI("simple/api.raml"))
    val profile                                          = filePath(platform.encodeURI("simple/profile.yaml"))
    val expected                                         = filePath(platform.encodeURI("simple/expected/fixed.yaml"))
    val server: LanguageServer                           = buildServer(diagnosticNotifier)
    withServer(server, buildInitArgs(workspacePath)) { server =>
      for {
        content <- platform.fetchContent(mainFileUri, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _       <- openFile(server)(mainFileUri, content)
        _       <- diagnosticNotifier.nextCall
        _       <- diagnosticNotifier.nextCall
        _ <- changeWorkspaceConfiguration(server)(
          changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profile)))
        _           <- diagnosticNotifier.nextCall // resolution diagnostics
        diagnostics <- diagnosticNotifier.nextCall // custom validation diagnostics
        _           <- changeFile(server)(mainFileUri, content.replace("type: string", "type: string\n       minLength: 1"), 1)
        _           <- diagnosticNotifier.nextCall // resolution diagnostics
        d           <- diagnosticNotifier.nextCall // custom validation diagnostics
        tmp         <- writeTemporaryFile(expected)(d.write)
        r           <- assertDifferences(tmp, expected)
      } yield {
        diagnostics.diagnostics should not be empty
        r
      }
    }
  }

  test("Should be able to apply multiple profiles") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(4000)
    val workspacePath                                    = filePath(platform.encodeURI("multiple-profiles"))
    val mainFileName                                     = "api.raml"
    val mainFileUri                                      = filePath(platform.encodeURI("multiple-profiles/api.raml"))
    val profile1                                         = filePath(platform.encodeURI("multiple-profiles/profile.yaml"))
    val profile2                                         = filePath(platform.encodeURI("multiple-profiles/max-endpoints.yaml"))
    val expected                                         = filePath(platform.encodeURI(s"multiple-profiles/expected/result.yaml"))

    val args = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profile1, profile2))

    val server: LanguageServer = buildServer(diagnosticNotifier)
    withServer(server, buildInitArgs(workspacePath)) { server =>
      for {
        content     <- platform.fetchContent(mainFileUri, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _           <- openFile(server)(mainFileUri, content)
        _           <- diagnosticNotifier.nextCall
        _           <- diagnosticNotifier.nextCall
        _           <- changeWorkspaceConfiguration(server)(args)
        _           <- diagnosticNotifier.nextCall // resolution diagnostics
        diagnostics <- diagnosticNotifier.nextCall // custom validation diagnostics
        tmp         <- writeTemporaryFile(expected)(diagnostics.write)
        r           <- assertDifferences(tmp, expected)
      } yield r
    }
  }

  test("Should be able to swap multiple profiles") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(4000)
    val workspacePath                                    = filePath(platform.encodeURI("multiple-profiles"))
    val mainFileName                                     = "api.raml"
    val mainFile                                         = filePath(platform.encodeURI("multiple-profiles/api.raml"))
    val profile1                                         = filePath(platform.encodeURI("multiple-profiles/profile.yaml"))
    val profile2                                         = filePath(platform.encodeURI("multiple-profiles/max-endpoints.yaml"))
    val expected                                         = filePath(platform.encodeURI(s"multiple-profiles/expected/swap.yaml"))
    def args(p: Set[String] = Set.empty)                 = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, p)

    def run(): Future[YDocument] = {
      for {
        _           <- diagnosticNotifier.nextCall // resolution diagnostics
        diagnostics <- diagnosticNotifier.nextCall // custom validation diagnostics
      } yield {
        diagnostics.yDocument
      }
    }
    val server: LanguageServer = buildServer(diagnosticNotifier)
    withServer(server, buildInitArgs(workspacePath)) { server =>
      for {
        content <- platform.fetchContent(mainFile, AMFGraphConfiguration.predefined()).map(_.stream.toString)
        _       <- openFile(server)(mainFile, content)
        _       <- run()
        _       <- changeWorkspaceConfiguration(server)(args(Set(profile1)))
        a       <- run()
        _       <- changeWorkspaceConfiguration(server)(args(Set(profile1, profile2)))
        b       <- run()
        _       <- changeWorkspaceConfiguration(server)(args(Set(profile2)))
        c       <- run()
        _       <- changeWorkspaceConfiguration(server)(args(Set(profile2, profile1)))
        d       <- run()
        _       <- changeWorkspaceConfiguration(server)(args())
        e       <- run()
        result  <- Future(YamlRender.render(Seq(a, b, c, d, e)))
        tmp     <- writeTemporaryFile(expected)(result)
        r       <- assertDifferences(tmp, expected)
      } yield r
    }
  }

}
