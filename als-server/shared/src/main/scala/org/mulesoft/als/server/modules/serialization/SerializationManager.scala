package org.mulesoft.als.server.modules.serialization

import java.util.UUID

import amf.core.model.document.{BaseUnit, Document}
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.BaseUnitListener
import org.mulesoft.als.server.modules.workspace.{CompilableUnit, DiagnosticsBundle}
import org.mulesoft.als.server.{ClientNotifierModule, RequestModule, SerializationProps}
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.amfmanager.{AmfParseResult, ParserHelper}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.amfmanager.BaseUnitImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SerializationManager[S](amfConf: AmfInstance, props: SerializationProps[S], logger: Logger)
    extends ClientNotifierModule[SerializationClientCapabilities, SerializationServerOptions]
    with BaseUnitListener
    with RequestModule[SerializationClientCapabilities, SerializationServerOptions] {

  private var enabled: Boolean = false

  override val `type`: SerializationConfigType.type = SerializationConfigType

  private def resolveAndSerialize(model: BaseUnit) = {
    val resolved = amfConf.parserHelper.editingResolve(model)
    val value    = props.newDocBuilder()
    ParserHelper.toJsonLD(resolved, value).map(_ => value)
  }

  /**
    * Called on new AST available
    *
    * @param ast  - AST
    * @param uuid - telemetry UUID
    */
  override def onNewAst(ast: (AmfParseResult, Map[String, DiagnosticsBundle]), uuid: String): Unit = {
    if (enabled) process(ast._1.baseUnit).foreach(s => props.alsClientNotifier.notifySerialization(s))
  }

  override def onRemoveFile(uri: String): Unit = {}

  override def applyConfig(config: Option[SerializationClientCapabilities]): SerializationServerOptions = {
    config.foreach(c => enabled = c.acceptsNotification)
    SerializationServerOptions(true)
  }

  private def process(ast: BaseUnit): Future[SerializationResult[S]] = {
    val cloned = ast.cloneUnit()
    resolveAndSerialize(cloned).map(b => SerializationResult(ast.identifier, b.result))
  }

  private def processRequest(uri: String): Future[SerializationResult[S]] = {
    val bu: Future[CompilableUnit] = unitAccessor match {
      case Some(ua) =>
        ua.getCU(uri, UUID.randomUUID().toString)
      case _ =>
        logger.warning("Unit accessor not configured", "SerializationManager", "RequestSerialization")
        Future.successful(CompilableUnit(uri, Document(), None, None, Nil))
    }

    bu.flatMap(cu => process(cu.unit))
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
