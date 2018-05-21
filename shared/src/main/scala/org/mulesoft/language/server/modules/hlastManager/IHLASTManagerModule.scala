package org.mulesoft.language.server.modules.hlastManager

import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.Future

/**
  * Manager module for high-level AST
  */
trait IHLASTManagerModule {

  /**
    * Module ID
    */
  val moduleId: String = "HL_AST_MANAGER"

  /**
    * Gets AST by uri
    * @param uri
    * @return
    */
  def forceGetCurrentAST(uri: String): Future[IProject]

  /**
    * Adds a listener to the new ASTs
    * @param listener
    * @param unsubscribe
    */
  def onNewASTAvailable(listener: IHLASTListener, unsubscribe: Boolean = false): Unit
}

object IHLASTManagerModule {

  /**
    * Module ID
    */
  val moduleId: String = "HL_AST_MANAGER"
}