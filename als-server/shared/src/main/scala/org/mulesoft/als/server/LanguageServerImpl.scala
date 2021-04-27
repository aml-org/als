package org.mulesoft.als.server

import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.server.feature.configuration.UpdateConfigurationParams
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.configuration.ConfigurationManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsInitializeParams, AlsInitializeResult}
import org.mulesoft.als.server.protocol.textsync.AlsTextDocumentSyncConsumer
import org.mulesoft.lsp.configuration.WorkspaceFolder
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LanguageServerImpl(val textDocumentSyncConsumer: AlsTextDocumentSyncConsumer,
                         val workspaceService: AlsWorkspaceService,
                         private val configuration: ConfigurationManager,
                         private val languageServerInitializer: LanguageServerInitializer,
                         private val requestHandlerMap: RequestMap,
                         logger: Logger)
    extends LanguageServer {

  override def initialize(params: AlsInitializeParams): Future[AlsInitializeResult] = {
    logger.debug(s"trace: ${params.trace}", "LanguageServerImpl", "initialize")
    logger.debug(s"rootUri: ${params.rootUri}", "LanguageServerImpl", "initialize")
    logger.debug(s"rootPath: ${params.rootPath}", "LanguageServerImpl", "initialize")
    logger.debug(s"workspaceFolders: ${params.workspaceFolders.getOrElse(Seq())}", "LanguageServerImpl", "initialize")
    logger.debug(s"alsConfiguration: ${params.alsConfiguration.map(_.toString).getOrElse("")}",
                 "LanguageServerImpl",
                 "initialize")
    logger.debug(s"capabilities: ${params.capabilities.toString}", "LanguageServerImpl", "initialize")

    params.alsConfiguration.foreach(c => {
      updateConfiguration(UpdateConfigurationParams(Option(c.getFormatOptions)))
    })
    configuration.updateDocumentChangesSupport(
      params.capabilities.workspace.flatMap(_.workspaceEdit).flatMap(_.documentChanges).contains(true))

    languageServerInitializer.initialize(params).flatMap { p =>
      val root: Option[String]                   = params.rootUri.flatMap(rootUriIfValid).orElse(params.rootPath)
      val workspaceFolders: Seq[WorkspaceFolder] = params.workspaceFolders.getOrElse(List())
      workspaceService
        .initialize((workspaceFolders :+ WorkspaceFolder(root, None)).toList)
        .map(_ => p)
    }
  }

  /**
    * if it is not a valid URI and a local file which we and AMF understand (file:), ignore it
    * @param rootUri
    * @return
    */
  private def rootUriIfValid(rootUri: String): Option[String] =
    if (rootUri.isValidFileUri)
      Some(rootUri)
    else {
      logger.warning(s"Not recognized $rootUri as a valid Root URI", "LanguageServerImpl", "initialize")
      None
    }

  override def initialized(): Unit = {
    // no further actions
  }

  override def shutdown(): Unit = {
    // no further actions at the moment, maybe shutdown managers?
  }

  override def exit(): Unit = {
    // no further actions at the moment, maybe shutdown managers?
  }

  override def resolveHandler[P, R](requestType: RequestType[P, R]): Option[RequestHandler[P, R]] =
    requestHandlerMap(requestType)

  override def updateConfiguration(params: UpdateConfigurationParams): Unit = {
    configuration.update(params)
  }
}
