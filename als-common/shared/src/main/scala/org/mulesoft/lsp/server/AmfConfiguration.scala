package org.mulesoft.lsp.server

import amf.client.plugins.AMFPlugin
import amf.core.AMF
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.common.{DirectoryResolver, FileUtils}
import org.mulesoft.amfmanager.{AmfParseResult, ParserHelper}

import scala.concurrent.Future

class AmfConfiguration(plugins: Seq[AMFPlugin], languageServerConf: LanguageServerSystemConf)
    extends AmfEnvHandler
    with LanguageServerSystemConf {

  def parseHelper: ParserHelper = new ParserHelper(platform = languageServerConf.platform, init())

  def parse(uri: String): Future[AmfParseResult] = parseHelper.parse(uri, languageServerConf.environment)

  def getEncodedUri(uri: String): String = FileUtils.getEncodedUri(uri, languageServerConf.platform)

  def getDecodedUri(uri: String): String = FileUtils.getDecodedUri(uri, languageServerConf.platform)

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
        amf.core.AMF.registerPlugin(AMFValidatorPlugin)
        amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
        plugins.foreach(amf.core.AMF.registerPlugin)
        val f = AMF.init()
        initialization = Some(f)
        f
    }
  }

  override def platform: Platform = languageServerConf.platform

  override def environment: Environment = languageServerConf.environment

  override def directoryResolver: DirectoryResolver = languageServerConf.directoryResolver
}

object AmfConfiguration {
  def apply(languageServerSystemConf: LanguageServerSystemConf): AmfConfiguration =
    new AmfConfiguration(Nil, languageServerSystemConf)
}

trait AmfEnvHandler {
  def init(): Future[Unit]
}

object DefaultAmfConfiguration extends AmfConfiguration(Nil, DefaultServerSystemConf)
