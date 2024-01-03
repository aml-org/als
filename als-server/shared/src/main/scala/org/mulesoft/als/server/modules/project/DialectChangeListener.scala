package org.mulesoft.als.server.modules.project

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.server.SerializationProps
import org.mulesoft.als.server.feature.serialization.SerializationConfigType
import org.mulesoft.als.server.modules.serialization.{BaseSerializationNotifier, RenderProps}
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DialectChangeListener[S](
    props: SerializationProps[S],
    configurationReader: AlsConfigurationReader,
    renderProps: RenderProps
) extends BaseSerializationNotifier[S](props, configurationReader, renderProps)
    with NewConfigurationListener {

  override val `type`: SerializationConfigType.type = SerializationConfigType

  private var notifiedDialects: Seq[String] = Seq.empty

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
    val dialectIndex = ast.extensions.map(d => d.id -> d).toMap
    val ids          = dialectIndex.keys.toList
    val newDialects  = ids.diff(notifiedDialects)
    notifiedDialects = ids
    newDialects.foreach { id =>
      notifyDialect(dialectIndex(id))
    }
  }

  private def notifyDialect(d: Dialect): Unit =
    serializeAndNotify(d, AMLConfiguration.predefined().withDialect(d))

  override def onRemoveFile(uri: String): Unit = {}

  override def initialize(): Future[Unit] = Future.successful()
}
