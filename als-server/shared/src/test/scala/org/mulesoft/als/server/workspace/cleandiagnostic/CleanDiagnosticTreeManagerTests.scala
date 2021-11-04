package org.mulesoft.als.server.workspace.cleandiagnostic

import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.modules.configuration.WorkspaceConfigurationProvider
import org.mulesoft.als.server.modules.diagnostic.CleanDiagnosticTreeManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.modules.workspace.WorkspaceContentManager
import org.mulesoft.als.server.workspace.extract.WorkspaceConfig
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.scalatest.AsyncFlatSpec

import scala.concurrent.{ExecutionContext, Future}

class CleanDiagnosticTreeManagerTests extends AsyncFlatSpec {
  override implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  behavior of "CleanDiagnosticTreeManager"

  it should "register configured dialects/semantics" in {
    val mainUri    = "file:///main.raml"
    val dialectUri = "file:///d1.yaml"
    val dRL = AmfConfigurationWrapper.resourceLoaderForFile(dialectUri,
                                                            """#%Dialect 1.0
        |name: MyDialect
        |version: 1.0.0
        |""".stripMargin)
    val mfRL = AmfConfigurationWrapper.resourceLoaderForFile(mainUri,
                                                             """#%RAML 1.0
        |title: test
        |version: 1
        |""".stripMargin)
    val configs =
      Map(mainUri -> WorkspaceConfig("file:///", mainUri, Set.empty, Set.empty, Set(dialectUri), Set.empty, None))

    for {
      myAmfConfiguration <- AmfConfigurationWrapper(Seq(mfRL, dRL))
      _                  <- new DummyCleanDiagnosticTreeManager(myAmfConfiguration, configs).validate(mainUri)
    } yield assert(myAmfConfiguration.dialects.exists(d => d.location().contains(dialectUri)))
  }

  class DummyCleanDiagnosticTreeManager(amfConfiguration: AmfConfigurationWrapper,
                                        configs: Map[String, WorkspaceConfig])
      extends CleanDiagnosticTreeManager(DummyTelemetryProvider,
                                         amfConfiguration,
                                         EmptyLogger,
                                         None,
                                         DummyConfigProvider) {

    /**
      * do not branch, used to check if the changes were applied
      */
    override protected def getCleanAmfWrapper: AmfConfigurationWrapper = amfConfiguration

    /**
      * inject configs for test
      */
    override protected def getWorkspaceConfig(uri: String): Future[Option[WorkspaceConfig]] =
      Future.successful(configs.get(uri))
  }

  object DummyConfigProvider extends WorkspaceConfigurationProvider {
    override def getWorkspaceConfiguration(uri: String): Future[(WorkspaceContentManager, Option[WorkspaceConfig])] =
      fail("getWorkspaceConfiguration should not be called from Dummy manager") // will override the specific get config
  }

  object DummyTelemetryProvider extends TelemetryManager(DummyClientNotifier, EmptyLogger)
  object DummyClientNotifier extends ClientNotifier {
    override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}

    override def notifyTelemetry(params: TelemetryMessage): Unit = {}
  }
}
