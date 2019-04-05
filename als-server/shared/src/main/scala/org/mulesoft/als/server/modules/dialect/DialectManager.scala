package org.mulesoft.als.server.modules.dialect

import amf.core.AMF
import amf.core.client.ParserConfig
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.server.Initializable
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.dialect.dialects.AsyncAPI
import org.mulesoft.als.server.platform.{ProxyContentPlatform, ServerPlatform}
import org.mulesoft.high.level.amfmanager.ParserHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class DialectManager(astManager: AstManager, platform: ServerPlatform, logger: Logger)
  extends Initializable {

  override def initialize(): Future[Unit] =
    amfInit()

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

          val proxyPlatform = new ProxyContentPlatform(platform, logger, d.files)
          val helper = ParserHelper(proxyPlatform)
          helper.parse(dialectCfg, proxyPlatform.defaultEnvironment)
        })
        Future.sequence(dialecOpts).map(_ => {})
      })
  }
}
