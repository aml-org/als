package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.common.AmfConfigurationPatcher
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.modules.configuration.WorkspaceConfigurationProvider
import org.mulesoft.als.server.modules.diagnostic.custom.CustomValidationManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.{
  DefaultProjectConfiguration,
  DefaultProjectConfigurationProvider,
  WorkspaceContentManager
}
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocument}
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EditorConfigurationState,
  EmptyProjectConfigurationState,
  ProjectConfigurationState
}
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.scalatest.{AsyncFlatSpec, stats}

import scala.concurrent.{ExecutionContext, Future}

class CleanDiagnosticTreeTest extends AsyncFlatSpec {

  override implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private val mainUri = "file:///main.raml"
  private val mfRL: ResourceLoader = AmfConfigurationPatcher.resourceLoaderForFile(mainUri,
                                                                                   """#%RAML 1.0
      |title: test
      |version: 1
      |""".stripMargin)

  private val profileUri = "file:///profile.yaml"
  private val vRL: ResourceLoader = AmfConfigurationPatcher.resourceLoaderForFile(
    profileUri,
    """#%Validation Profile 1.0
      |profile: Test
      |violation:
      |  - validation1
      |validations:
      |  validation1:
      |    targetClass: apiContract.Request
      |    message: Scalars in parameters must have minLength defined
      |    propertyConstraints:
      |      apiContract.parameter / shapes.schema:
      |        nested:
      |          propertyConstraints:
      |            shacl.minLength:
      |              minCount: 1""".stripMargin
  )

  behavior of "CleanDiagnosticTreeManager"

  it should "register configured dialects/semantics" in {
    val dialectUri = "file:///d1.yaml"
    val dRL = AmfConfigurationPatcher.resourceLoaderForFile(dialectUri,
                                                            """#%Dialect 1.0
        |name: MyDialect
        |version: 1.0.0
        |""".stripMargin)
    val configs =
      Map(
        mainUri -> new ProjectConfiguration("file:///",
                                            Some(mainUri),
                                            Set.empty,
                                            Set.empty,
                                            Set(dialectUri),
                                            Set.empty))

    for {
      state <- new DefaultProjectConfigurationProvider(DummyEnvironmentProvider, EditorConfiguration(), EmptyLogger)
        .newProjectConfiguration(
          new ProjectConfiguration("file:///", Some(mainUri), Set.empty, Set.empty, Set(dialectUri), Set.empty))
      d <- {
        val manager = new DummyCleanDiagnosticTreeManager(
          Map(
            mainUri -> ALSConfigurationState(
              EditorConfigurationState(Seq(dRL, mfRL), Nil, Nil, syntaxPlugin = Nil, validationPlugin = Nil),
              state,
              None)))
        manager.getConfiguration(mainUri)
      }
    } yield assert(d.dialects.exists(d => d.location().contains(dialectUri)))
  }

  it should "register configured validation profiles" in {

    for {
      state <- new DefaultProjectConfigurationProvider(DummyEnvironmentProvider, EditorConfiguration(), EmptyLogger)
        .newProjectConfiguration(
          new ProjectConfiguration("file:///", Some(mainUri), Set.empty, Set(profileUri), Set.empty, Set.empty))
      d <- {
        val manager = new DummyCleanDiagnosticTreeManager(
          Map(mainUri -> ALSConfigurationState(EditorConfigurationState.empty, state, None)))
        manager.getConfiguration(mainUri)
      }
    } yield {
      assert(d.projectState.profiles.exists(_.path == profileUri))
      assert(d.projectState.config.validationDependency.contains(profileUri))
    }
  }

  class DummyCleanDiagnosticTreeManager(configs: Map[String, ALSConfigurationState],
                                        customValidationManager: Option[CustomValidationManager] = None)
      extends CleanDiagnosticTreeManager(DummyTelemetryProvider,
                                         DummyEnvironmentProvider,
                                         EmptyLogger,
                                         customValidationManager,
                                         DummyConfigProvider) {

    val emptyConfig: ALSConfigurationState = ALSConfigurationState(
      EditorConfigurationState(Nil, Nil, Nil, syntaxPlugin = Nil, validationPlugin = Nil),
      EmptyProjectConfigurationState,
      None)

    override protected def getWorkspaceConfig(uri: String): Future[ALSConfigurationState] =
      Future.successful(configs.getOrElse(uri, emptyConfig))

    def getConfiguration(uri: String): Future[ALSConfigurationState] = getWorkspaceConfig(uri)
  }

  object DummyConfigProvider extends WorkspaceConfigurationProvider {
    override def getWorkspaceConfiguration(uri: String): Future[(WorkspaceContentManager, ProjectConfiguration)] =
      fail("getWorkspaceConfiguration should not be called from Dummy manager") // will override the specific get config

    override def getConfigurationState(uri: String): Future[ALSConfigurationState] =
      fail("getConfigurationState should not be called from Dummy manager")
  }

  object DummyEnvironmentProvider extends EnvironmentProvider {
    override def getResourceLoader: ResourceLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future.successful(new Content("", resource))
      override def accepts(resource: String): Boolean       = false
    }
    override def openedFiles: Seq[String]                 = Seq.empty
    override def filesInMemory: Map[String, TextDocument] = Map()
    override def initialize(): Future[Unit]               = Future.successful()
  }

  object DummyTelemetryProvider extends TelemetryManager(DummyClientNotifier, EmptyLogger)
  object DummyClientNotifier extends ClientNotifier {
    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}
  }
}
