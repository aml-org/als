package org.mulesoft.als.server.modules.project

import amf.aml.client.scala.AMLConfiguration
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.feature.serialization.SerializationConfigType
import org.mulesoft.als.server.modules.serialization.BaseSerializationNotifier
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileChangeListener[S](
    props: SerializationProps[S],
    configurationReader: AlsConfigurationReader
) extends BaseSerializationNotifier[S](props, configurationReader)
    with NewConfigurationListener {

  override val `type`: SerializationConfigType.type = SerializationConfigType

  private var notifiedProfiles: Seq[String] = Seq.empty

  /** Called on new AST available
    *
    * @param ast
    *   \- AST
    * @param uuid
    *   \- telemetry UUID
    */
  override def onNewAst(ast: ProjectConfigurationState, uuid: String): Future[Unit] =
    if (isActive) executeNotification(ast) else Future.successful()

  private def executeNotification(ast: ProjectConfigurationState): Future[Unit] = Future {
    val profileIndex = ast.profiles.map(vp => vp.model.id -> vp).toMap
    val ids          = profileIndex.keys.toList
    val newProfiles  = ids.diff(notifiedProfiles)
    notifiedProfiles = ids
    newProfiles.foreach { id =>
      notifyProfile(profileIndex(id))
    }
  }

  private def notifyProfile(vp: ValidationProfile): Unit =
    serializeAndNotify(vp.model, AMLConfiguration.predefined().withDialect(vp.definedBy))

  override def onRemoveFile(uri: String): Unit = {}

  override def initialize(): Future[Unit] = Future.successful()
}
