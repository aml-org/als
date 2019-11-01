package org.mulesoft.als.server

import org.mulesoft.lsp.server.LanguageServer
import org.mulesoft.lsp.textsync.TextDocumentSyncConsumer
import org.mulesoft.lsp.{Initializable, InitializableModule}

import scala.collection.mutable

class LanguageServerBuilder(private val textDocumentSyncConsumer: TextDocumentSyncConsumer) {
  private val initializableModules = mutable.ListBuffer[InitializableModule[_, _]]()
  private val requestModules       = mutable.ListBuffer[RequestModule[_, _]]()
  private val initializables       = mutable.ListBuffer[Initializable]()

  def addInitializableModule[C, S](module: InitializableModule[C, S]): this.type = {
    initializableModules += module
    this
  }

  def addRequestModule[C, S](module: RequestModule[C, S]): this.type = {
    requestModules += module
    this
  }

  def addInitializable(initializable: Initializable): this.type = {
    initializables += initializable
    this
  }

  def build(): LanguageServer = {

    val configMap = (requestModules ++ initializableModules :+ textDocumentSyncConsumer)
      .foldLeft(ConfigMap.empty)((result, value) => result.put(value.`type`, value))

    val handlerMap = requestModules
      .flatMap(_.getRequestHandlers)
      .foldLeft(RequestMap.empty)((result, value) => result.put(value.`type`, value))

    val allInitializables = initializables ++ requestModules ++ initializableModules :+ textDocumentSyncConsumer

    new LanguageServerImpl(textDocumentSyncConsumer,
                           new LanguageServerInitializer(configMap, allInitializables),
                           handlerMap)
  }
}
