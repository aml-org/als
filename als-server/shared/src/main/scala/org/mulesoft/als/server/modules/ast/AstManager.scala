package org.mulesoft.als.server.modules.ast

import java.util.UUID

import amf.core.AMF
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.Initializable
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * AST manager
  */
class AstManager(private val baseEnvironment: Environment,
                 private val telemetryProvider: TelemetryProvider,
                 private val platform: Platform,
                 private val logger: Logger)
    extends Initializable {

  private var initialized: Option[Future[Unit]] = None

  private val reconciler: Reconciler = new Reconciler(logger, 500)

  // if i initialize this when creating server, why is trying to initialize each parse? has sense? Maybe it would be more simply just check the initialization each parse
  override def initialize(): Future[Unit] =
    amfInit()

  def init(): Future[Unit] = {
    initialized match {
      case Some(f) => f
      case _ =>
        val f = amfInit()
        initialized = Some(f)
        f
    }
  }

  def amfInit(): Future[Unit] = {
    val telemetryUUID = UUID.randomUUID().toString
    telemetryProvider.addTimedMessage("Initialize AMF begin", MessageTypes.BEGIN_AMF_INIT, "", telemetryUUID)
    amf.core.AMF.registerPlugin(AMLPlugin)
    amf.core.AMF.registerPlugin(Raml10Plugin)
    amf.core.AMF.registerPlugin(Raml08Plugin)
    amf.core.AMF.registerPlugin(Oas20Plugin)
    amf.core.AMF.registerPlugin(Oas30Plugin)
    amf.core.AMF.registerPlugin(AMFValidatorPlugin)
    amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
    AMF
      .init()
      .map(i => {
        telemetryProvider.addTimedMessage("Initialize AMF end", MessageTypes.END_AMF_INIT, "", telemetryUUID)
        i
      })
  }

  def forceGetCurrentAST(uri: String, uuid: String): Future[BaseUnit] = {
    init()
      .flatMap(_ => {
        parse(uri, telemetryProvider, uuid)
      })
  }

  def onIndexDialect(uri: String, content: Option[String]): Unit = {
    val telemetryUUID: String = UUID.randomUUID().toString
    logger.debug(s"Dialect $uri is called to index", "ASTManager", "indexDialect")
    telemetryProvider.addTimedMessage("dialect indexed", MessageTypes.INDEX_DIALECT, uri, telemetryUUID)

    ParserHelper(platform)
      .indexDialect(uri, content)
      .foreach(_ => {
        logger.debug(s"Dialect $uri has been indexed", "ASTManager", "indexDialect")
      })
  }

  def parse(uri: String, telemetryProvider: TelemetryProvider, uuid: String): Future[BaseUnit] = {
    telemetryProvider.addTimedMessage("Begin parsing", MessageTypes.BEGIN_PARSE, uri, uuid)
    val amfURI = FileUtils.getDecodedUri(uri, platform)

    logger.debugDetail(s"Protocol uri is $amfURI", "ASTManager", "parse")
    val startTime = System.currentTimeMillis()

    val helper = ParserHelper(platform)

    helper.parse(amfURI, baseEnvironment).map { result =>
      val endTime = System.currentTimeMillis()
      logger.debugDetail(s"It took ${endTime - startTime} milliseconds to build AMF ast", "ASTManager", "parse")
      telemetryProvider.addTimedMessage("End parsing", MessageTypes.END_PARSE, uri, uuid)
      result
    }
  }

}
