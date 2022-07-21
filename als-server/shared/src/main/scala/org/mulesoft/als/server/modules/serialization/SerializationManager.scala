package org.mulesoft.als.server.modules.serialization

import amf.aml.client.scala.AMLConfiguration
import amf.apicontract.client.scala.model.document.{Extension, Overlay}
import amf.core.client.scala.model.document.{BaseUnit, Document}
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.modules.ast.ResolvedUnitListener
import org.mulesoft.als.server.modules.common.reconciler.Runnable
import org.mulesoft.als.server.{RequestModule, SerializationProps}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.AmfResolvedUnit
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class SerializationManager[S](
    telemetryProvider: TelemetryProvider,
    editorConfiguration: EditorConfiguration,
    configurationReader: AlsConfigurationReader,
    props: SerializationProps[S],
    override val logger: Logger
) extends BaseSerializationNotifier[S](props, configurationReader, logger)
    with ResolvedUnitListener
    with RequestModule[SerializationClientCapabilities, SerializationServerOptions] {
  type RunType = SerializationRunnable

  override val `type`: SerializationConfigType.type = SerializationConfigType

  override protected def runnable(ast: AmfResolvedUnit, uuid: String): SerializationRunnable =
    new SerializationRunnable(ast.baseUnit.identifier, ast, uuid)

  override def isActive: Boolean = enabled

  private val baseConfiguration = AMLConfiguration.predefined()

  def serializeAndNotifyResolved(ast: AmfResolvedUnit): Future[Unit] =
    ast.resolvedUnit.map(_.baseUnit).map(serializeAndNotify(_, baseConfiguration))

  override def onRemoveFile(uri: String): Unit = {
    /* No action required */
  }

  private def getUnitFromResolved(unit: BaseUnit, uri: String): BaseUnit =
    if (unit.identifier == uri) unit
    else
      throw new Exception(s"Unreachable code - getUnitFromResolved $uri in BaseUnit ${unit.location}")

  private def processRequest(uri: String): Future[SerializationResult[S]] = {
    val bu: Future[BaseUnit] = unitAccessor match {
      case Some(ua) =>
        ua.getLastUnit(uri, UUID.randomUUID().toString)
          .flatMap { r =>
            logger.debug(s"Serialization uri: $uri", "SerializationManager", "processRequest")
            if (r.baseUnit.isInstanceOf[Extension] || r.baseUnit.isInstanceOf[Overlay])
              r.latestBU
            else r.latestBU.map(getUnitFromResolved(_, uri))
          }
          .recoverWith { case e: Exception =>
            logger.warning(e.getMessage, "SerializationManager", "RequestSerialization")
            Future.successful(Document().withId("error"))
          }
      case _ =>
        logger.warning("Unit accessor not configured", "SerializationManager", "RequestSerialization")
        Future.successful(Document().withId("error"))
    }
    bu.map(serialize(_, baseConfiguration))
  }

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[SerializationParams, SerializationResult[S]] {
      override def `type`: props.requestType.type = props.requestType

      override def task(params: SerializationParams): Future[SerializationResult[S]] =
        processRequest(params.documentIdentifier.uri)

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: SerializationParams): String = "SerializationManager"

      override protected def beginType(params: SerializationParams): MessageTypes = MessageTypes.BEGIN_SERIALIZATION

      override protected def endType(params: SerializationParams): MessageTypes = MessageTypes.END_SERIALIZATION

      override protected def msg(params: SerializationParams): String =
        s"Requested serialization for ${params.documentIdentifier.uri}"

      override protected def uri(params: SerializationParams): String = params.documentIdentifier.uri

      override protected val empty: Option[SerializationResult[S]] = None
    }
  )

  override protected def onSuccess(uuid: String, uri: String): Unit =
    logger.debug(s"Scheduled success $uuid", "SerializationManager", "onSuccess")

  override protected def onFailure(uuid: String, uri: String, t: Throwable): Unit =
    logger.warning(s"${t.getMessage} - uuid: $uuid", "SerializationManager", "onFailure")

  override protected def onNewAstPreprocess(resolved: AmfResolvedUnit, uuid: String): Unit =
    logger.debug(s"onNewAst serialization manager $uuid", "SerializationManager", "onNewAstPreprocess")

  class SerializationRunnable(var uri: String, ast: AmfResolvedUnit, uuid: String) extends Runnable[Unit] {
    private var canceled = false

    private val kind = "SerializationRunnable"

    def run(): Promise[Unit] = {
      val promise = Promise[Unit]()

      def innerSerialize(): Future[Unit] =
        serializeAndNotifyResolved(ast) andThen {
          case Success(report) => promise.success(report)

          case Failure(error) => promise.failure(error)
        }

      telemetryProvider.timeProcess(
        "Serialize notification",
        MessageTypes.BEGIN_SERIALIZATION,
        MessageTypes.END_SERIALIZATION,
        s"Scheduled Serialize notification for ${ast.baseUnit.identifier}",
        ast.baseUnit.identifier,
        innerSerialize,
        uuid
      )

      promise
    }

    def conflicts(other: Runnable[Any]): Boolean =
      other.asInstanceOf[SerializationRunnable].kind == kind && uri == other
        .asInstanceOf[SerializationRunnable]
        .uri

    def cancel(): Unit = {
      canceled = true
    }

    def isCanceled: Boolean = canceled
  }
}
