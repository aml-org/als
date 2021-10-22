package org.mulesoft.als.server.modules.serialization

import amf.core.internal.remote.Spec
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.UnitAccessor
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConversionManager(unitAccessor: UnitAccessor[CompilableUnit],
                        telemetryProvider: TelemetryProvider,
                        amfConfiguration: AmfConfigurationWrapper,
                        logger: Logger)
    extends RequestModule[ConversionClientCapabilities, ConversionRequestOptions] {

  private var enabled = false

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[ConversionParams, SerializedDocument] {
      override def `type`: ConversionRequestType.type = ConversionRequestType

      override def task(params: ConversionParams): Future[SerializedDocument] = {
        if (!enabled) logger.warning("Request conversion with manager disabled", "ConversionManager", "convert")
        onSerializationRequest(params.uri, params.target, params.syntax)
      }

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: ConversionParams): String = "ConversionManager"

      override protected def beginType(params: ConversionParams): MessageTypes = MessageTypes.BEGIN_CONVERSION

      override protected def endType(params: ConversionParams): MessageTypes = MessageTypes.END_CONVERSION

      override protected def msg(params: ConversionParams): String =
        s"Requested conversion from ${params.uri}\n\t[${params.syntax.getOrElse(".")} -> ${params.target}]"

      override protected def uri(params: ConversionParams): String = params.uri

      /**
        * If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[SerializedDocument] = None
    }
  )

  private def onSerializationRequest(uri: String, target: String, syntax: Option[String]): Future[SerializedDocument] = {
    unitAccessor
      .getLastUnit(uri, UUID.randomUUID().toString)
      .flatMap(_.getLast) map { cu => // should check the origin?
      SerializedDocument(cu.unit.identifier,
                         amfConfiguration
                           .convertTo(cu.unit, Spec(target), syntax))
    }
  }

  override val `type`: ConversionConfigType.type = ConversionConfigType

  override def initialize(): Future[Unit] =
    Future.successful()

  override def applyConfig(config: Option[ConversionClientCapabilities]): ConversionRequestOptions = {
    config.foreach(c => enabled = c.supported)
    ConversionRequestOptions(supported)
  }

  private val supported = Seq(
    RAML10ConvesionToOAS20Config,
    RAML10ConvesionToOAS30Config,
    OAS20ConvesionToOAS30Config,
    OAS20ConvesionToRAML10Config,
    OAS30ConvesionToRAML10Config,
    AsyncApi2SyntaxConversionConfig,
    OAS20SyntaxConversionConfig,
    OAS30SyntaxConversionConfig
  )
}
