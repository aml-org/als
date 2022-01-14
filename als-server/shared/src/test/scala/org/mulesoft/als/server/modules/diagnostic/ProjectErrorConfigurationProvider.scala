package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.workspace.DefaultProjectConfigurationProvider
import org.mulesoft.amfintegration.amfconfiguration.{EditorConfigurationProvider, ProjectConfigurationState}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectErrorConfigurationProvider(editorConfiguration: EditorConfigurationProvider,
                                        logger: Logger,
                                        error: AMFValidationResult)
    extends DefaultProjectConfigurationProvider(DummyEnvironmentProvider, editorConfiguration, logger) {
  private var reportError = true

  def setReportError(v: Boolean): Unit = this.reportError = v

  override def getProjectInfo(folder: String): Option[Future[ProjectConfigurationState]] =
    super
      .getProjectInfo(folder)
      .map(_.map(state => {
        new ProjectConfigurationState(
          state.extensions,
          state.profiles,
          state.config,
          state.results,
          state.resourceLoaders,
          if (reportError) Seq(error) else Seq()
        ) {
          override def cache: Seq[BaseUnit] = Seq.empty
        }
      }))
}
