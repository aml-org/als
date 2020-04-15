package org.mulesoft.als.server.modules.serialization

import java.util.UUID

import amf.core.model.document.{BaseUnit, Document}
import amf.plugins.document.webapi.model.{Extension, Overlay}
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.{BaseUnitListener, BaseUnitListenerParams}
import org.mulesoft.als.server.{ClientNotifierModule, RequestModule, SerializationProps}
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.feature.RequestHandler

import org.mulesoft.amfmanager.{AmfParseResult, ParserHelper}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.amfmanager.AmfImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SerializationManager[S](amfConf: AmfInstance, props: SerializationProps[S], logger: Logger)
    extends ClientNotifierModule[SerializationClientCapabilities, SerializationServerOptions]
    with BaseUnitListener
    with RequestModule[SerializationClientCapabilities, SerializationServerOptions] {

  private var enabled: Boolean = false

  override val `type`: SerializationConfigType.type = SerializationConfigType

  private def resolveAndSerialize(resolved: BaseUnit) = {
    val value = props.newDocBuilder()
    ParserHelper.toJsonLD(resolved, value).map(_ => value)
  }

  /**
    * Called on new AST available
    *
    * @param ast  - AST
    * @param uuid - telemetry UUID
    */
  override def onNewAst(ast: BaseUnitListenerParams, uuid: String): Unit =
    if (enabled)
      ast
        .resolvedUnit()
        .flatMap(_.latestBU)
        .flatMap(process)
        .foreach(s => props.alsClientNotifier.notifySerialization(s))

  override def onRemoveFile(uri: String): Unit = {
    /* No action required */
  }

  override def applyConfig(config: Option[SerializationClientCapabilities]): SerializationServerOptions = {
    config.foreach(c => enabled = c.acceptsNotification)
    SerializationServerOptions(true)
  }

  private def process(ast: BaseUnit): Future[SerializationResult[S]] =
    resolveAndSerialize(ast).map(b => SerializationResult(ast.identifier, b.result))

  private def getUnitFromResolved(unit: BaseUnit, uri: String): BaseUnit =
    if (unit.identifier == uri) unit
    else
      unit.flatRefs.find(_.identifier == uri) match {
        case Some(u) => u
        case None    => throw new Exception(s"Unreachable code - getUnitFromResolved $uri in BaseUnit ${unit.id}")
      }

  private def processRequest(uri: String): Future[SerializationResult[S]] = {
    val bu: Future[BaseUnit] = unitAccessor match {
      case Some(ua) =>
        ua.getResolved(uri, UUID.randomUUID().toString)
          .flatMap { r =>
            if (r.originalUnit.isInstanceOf[Extension] || r.originalUnit.isInstanceOf[Overlay])
              r.latestBU
            else r.latestBU.map(getUnitFromResolved(_, uri))
          }
          .recoverWith {
            case e: Exception =>
              logger.warning(e.getMessage, "SerializationManager", "RequestSerialization")
              Future.successful(Document())
          }
      case _ =>
        logger.warning("Unit accessor not configured", "SerializationManager", "RequestSerialization")
        Future.successful(Document())
    }
    bu.flatMap(process)
  }

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[SerializationParams, SerializationResult[S]] {
      override def `type`: props.requestType.type = props.requestType

      override def apply(params: SerializationParams): Future[SerializationResult[S]] =
        processRequest(params.textDocument.uri)

    }
  )
}
