package org.mulesoft.als.server.modules.project

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.server._
import org.mulesoft.als.server.client.platform.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.feature.diagnostic.CustomValidationClientCapabilities
import org.mulesoft.als.server.feature.serialization.SerializationClientCapabilities
import org.mulesoft.als.server.modules.serialization.SerializationManager
import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.workspace.ChangesWorkspaceConfiguration
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.yaml.builder.{DocBuilder, JsonOutputBuilder}

import java.io.StringWriter
import scala.concurrent.ExecutionContext

class ProfileConfigurationChangeListenerTest
    extends LanguageServerBaseTest
    with ChangesWorkspaceConfiguration
    with FileAssertionTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override val initializeParams: AlsInitializeParams = AlsInitializeParams(
    Some(AlsClientCapabilities(serialization = Some(SerializationClientCapabilities(true)))),
    Some(TraceKind.Off)
  )

  override def rootPath: String = "project"

  val workspacePath: String = filePath(platform.encodeURI("project"))
  val mainFileName: String  = "project/api.raml"
  val profileUri: String    = filePath(platform.encodeURI("project/profile.yaml"))
  val profile2Uri: String   = filePath(platform.encodeURI("project/profile2.yaml"))
  val profile3: String      = filePath(platform.encodeURI("project/profile3.yaml"))

  def buildInitParams(workspacePath: String): AlsInitializeParams =
    buildInitParams(Some(workspacePath))

  def buildInitParams(workspacePath: Option[String] = None): AlsInitializeParams =
    AlsInitializeParams(
      Some(
        AlsClientCapabilities(
          customValidations = Some(CustomValidationClientCapabilities(true)),
          serialization = Some(SerializationClientCapabilities(true))
        )
      ),
      Some(TraceKind.Off),
      rootUri = workspacePath,
      hotReload = Some(true)
    )

  def buildServer(
      serializationProps: SerializationProps[StringWriter],
      notifier: ClientNotifier = new MockDiagnosticClientNotifier,
      withDiagnostics: Boolean = false
  ): LanguageServer = {
    val factoryBuilder: WorkspaceManagerFactoryBuilder =
      new WorkspaceManagerFactoryBuilder(notifier, logger, EditorConfiguration())
    val dm = factoryBuilder.buildDiagnosticManagers(Some(DummyProfileValidator))
    val serializationManager: SerializationManager[StringWriter] =
      factoryBuilder.serializationManager(serializationProps)
    val pc = factoryBuilder.profileNotificationConfigurationListener(serializationProps)

    val factory: WorkspaceManagerFactory = factoryBuilder.buildWorkspaceManagerFactory()

    val builder =
      new LanguageServerBuilder(
        factory.documentManager,
        factory.workspaceManager,
        factory.configurationManager,
        factory.resolutionTaskManager
      )
    builder.addInitializableModule(serializationManager)
    if (withDiagnostics) dm.foreach(m => builder.addInitializableModule(m))
    builder.addRequestModule(serializationManager)
    builder.build()
  }

  def buildSerializationProps(alsClient: AlsClientNotifier[StringWriter]) =
    new SerializationProps[StringWriter](alsClient) {
      override def newDocBuilder(prettyPrint: Boolean): DocBuilder[StringWriter] =
        JsonOutputBuilder(prettyPrint)
    }

  test("Should notify when a profile is added") {
    val alsClient: MockAlsClientNotifier                     = new MockAlsClientNotifier(3000)
    val serializationProps: SerializationProps[StringWriter] = buildSerializationProps(alsClient)
    val args = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))

    withServer(buildServer(serializationProps), buildInitParams(workspacePath)) { server =>
      for {
        _ <- changeWorkspaceConfiguration(server)(args)
        s <- alsClient.nextCall.map(_.model.toString)
      } yield {
        s.contains("Test profile 1") should be(true)
        s.contains("Test profile 2") should be(false)
      }
    }
  }

  test("Should notify when a second profile is added") {
    val alsClient: MockAlsClientNotifier                     = new MockAlsClientNotifier(3000)
    val serializationProps: SerializationProps[StringWriter] = buildSerializationProps(alsClient)
    val initialArgs    = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri))
    val addProfileArgs = changeConfigArgs(Some(mainFileName), workspacePath, Set.empty, Set(profileUri, profile2Uri))

    withServer(buildServer(serializationProps), buildInitParams(workspacePath)) { server =>
      for {
        _ <- changeWorkspaceConfiguration(server)(initialArgs)
        _ <- alsClient.nextCall
        _ <- changeWorkspaceConfiguration(server)(addProfileArgs)
        s <- alsClient.nextCall.map(_.model.toString)
      } yield {
        s.contains("Test Profile 2") should be(true)
        s.contains("Test profile 1") should be(false)
      }
    }
  }

}
