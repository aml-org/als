package org.mulesoft.als.server.modules.dialect

import amf.core.AMF
import amf.core.client.ParserConfig
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.common.EnvironmentPatcher
import org.mulesoft.als.server.Initializable
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.dialect.dialects.AsyncAPI
import org.mulesoft.high.level.amfmanager.ParserHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class DialectManager(astManager: AstManager, baseEnvironment: Environment, platform: Platform, logger: Logger)
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

        val dialectOpts = dialects.map(dialect => {

          val dialectCfg = new ParserConfig(
            Some(ParserConfig.PARSE),
            Some(dialect.rootUrl),
            Some("AML 1.0"),
            Some("application/yaml"),
            None,
            Some("AMF"),
            Some("application/json+ld")
          )

          val helper = ParserHelper(platform)
          helper.parse(dialectCfg, EnvironmentPatcher.patch(baseEnvironment, dialect.files.toMap))
        })
        Future.sequence(dialectOpts).map(_ => {})
      })
  }
}
