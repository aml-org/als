package org.mulesoft.als.server.modules.serialization

import java.util.UUID

import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.serialization._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.UnitRepositoriesManager
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.amfmanager.BaseUnitImplicits._
import org.mulesoft.lsp.feature.RequestHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConversionManager(unitAccesor: UnitRepositoriesManager, amfInstance: AmfInstance, logger: Logger)
    extends RequestModule[ConversionClientCapabilities, ConversionRequestOptions] {

  private var enabled = false

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[ConversionParams, SerializedDocument] {
      override def `type`: ConversionRequestType.type = ConversionRequestType

      override def apply(params: ConversionParams): Future[SerializedDocument] = {
        if (!enabled) logger.warning("Request conversion with manager disabled", "ConversionManager", "convert")
        onSerializationRequest(params.uri, params.target, params.syntax)
      }
    }
  )

  private def onSerializationRequest(uri: String, target: String, syntax: Option[String]): Future[SerializedDocument] = {
    unitAccesor.getCU(uri, UUID.randomUUID().toString).flatMap(_.getLast) flatMap { cu =>
      val clone = cu.unit.cloneUnit()
      amfInstance.parserHelper
        .convertTo(clone, target, syntax) // should check the origin?
        .map(s => SerializedDocument(clone.identifier, s))
    }
  }

  override val `type`: ConversionConfigType.type = ConversionConfigType

  override def initialize(): Future[Unit] = amfInstance.init()

  override def applyConfig(config: Option[ConversionClientCapabilities]): ConversionRequestOptions = {
    config.foreach(c => enabled = c.supported)
    ConversionRequestOptions(supported)
  }

  private val supported = Seq(
    RAML10ConvesionToOAS20Config,
    RAML10ConvesionToOAS30Config,
    OAS20ConvesionToOAS30Config,
    OAS20ConvesionToRAML10Config,
    OAS30ConvesionToRAML10Config
  )
}
