package org.mulesoft.amfmanager

import amf.core.AMF
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin

import scala.concurrent.Future
object AmfInitializationHandler {

  private var initialization: Option[Future[Unit]] = None

  def init(): Future[Unit] = synchronized {
    initialization match {
      case Some(f) => f
      case _ =>
        amf.core.AMF.registerPlugin(AMLPlugin)
        amf.core.AMF.registerPlugin(Raml10Plugin)
        amf.core.AMF.registerPlugin(Raml08Plugin)
        amf.core.AMF.registerPlugin(Oas20Plugin)
        amf.core.AMF.registerPlugin(Oas30Plugin)
        amf.core.AMF.registerPlugin(AMFValidatorPlugin)
        amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
        val f = AMF.init()
        initialization = Some(f)
        f
    }
  }
}
