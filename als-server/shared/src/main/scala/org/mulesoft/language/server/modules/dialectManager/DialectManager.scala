package org.mulesoft.language.server.modules.dialectManager

import amf.core.AMF
import amf.core.client.ParserConfig
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.language.server.core.platform.ProxyContentPlatform
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.astManager.IASTManagerModule
import org.mulesoft.language.server.modules.dialectManager.dialects.AsyncAPI

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Try}

/**
  * AST manager
  */
class DialectManager extends AbstractServerModule with IDialectManagerModule {

  val moduleDependencies: Array[String] = Array(IASTManagerModule.moduleId)

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {
      amfInit()
      Success(this)
    } else {
      superLaunch
    }
  }

  def dialects: Seq[IBundledProject] = Seq(AsyncAPI)

  def amfInit(): Future[Unit] = {
    amf.core.AMF.registerPlugin(AMLPlugin)
    amf.core.AMF.registerPlugin(Raml10Plugin)
    amf.core.AMF.registerPlugin(Raml08Plugin)
    amf.core.AMF.registerPlugin(Oas20Plugin)
    amf.core.AMF.registerPlugin(Oas30Plugin)
    amf.core.AMF.registerPlugin(AMFValidatorPlugin)
    amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
    AMF
      .init()
      .flatMap(_ => {

        val dialecOpts = dialects.map(d => {

          var dialectCfg = new ParserConfig(
            Some(ParserConfig.PARSE),
            Some(d.rootUrl),
            Some("AML 1.0"),
            Some("application/yaml"),
            None,
            Some("AMF"),
            Some("application/json+ld")
          )

          val proxyPlatform = new ProxyContentPlatform(platform, d.files)
          val helper        = ParserHelper(proxyPlatform)
          helper.parse(dialectCfg, proxyPlatform.defaultEnvironment)
        })
        Future.sequence(dialecOpts).map(_ => {})
      })
  }

  override def stop(): Unit = {

    super.stop()
  }
}
