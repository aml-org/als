package org.mulesoft.amfintegration

import amf.client.plugins.AMFPlugin
import amf.core.AMF
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Async20Plugin, Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.{CompilerEnvironment, ModelBuilder}
import org.mulesoft.amfmanager.{AmfParseResult, ParserHelper}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// todo: move to another module
class AmfInstance(plugins: Seq[AMFPlugin], platform: Platform, environment: Environment)
    extends CompilerEnvironment[AmfParseResult, Environment] {

  def parserHelper                               = new ParserHelper(platform, init())
  def parse(uri: String): Future[AmfParseResult] = parserHelper.parse(uri, environment)

  private var initialization: Option[Future[Unit]] = None

  override def init(): Future[Unit] = synchronized {
    initialization match {
      case Some(f) => f
      case _ =>
        amf.core.AMF.registerPlugin(AMLPlugin)
        amf.core.AMF.registerPlugin(Raml10Plugin)
        amf.core.AMF.registerPlugin(Raml08Plugin)
        amf.core.AMF.registerPlugin(Oas20Plugin)
        amf.core.AMF.registerPlugin(Oas30Plugin)
        amf.core.AMF.registerPlugin(Async20Plugin)
        amf.core.AMF.registerPlugin(AMFValidatorPlugin)
        amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
        plugins.foreach(amf.core.AMF.registerPlugin)
        val f = AMF.init()
        initialization = Some(f)
        f
    }
  }

  override def modelBuiler(): ModelBuilder[AmfParseResult, Environment] = parserHelper
}

object AmfInstance extends PlatformSecrets {
  def apply(environment: Environment): AmfInstance                     = apply(platform, environment)
  def apply(platform: Platform, environment: Environment): AmfInstance = new AmfInstance(Nil, platform, environment)
  val default: AmfInstance                                             = new AmfInstance(Nil, platform, Environment())
}
