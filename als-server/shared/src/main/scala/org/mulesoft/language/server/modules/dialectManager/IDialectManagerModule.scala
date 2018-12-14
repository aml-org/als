package org.mulesoft.language.server.modules.dialectManager

import amf.core.model.document.BaseUnit
import org.mulesoft.language.server.core.IServerIOCModule

import scala.concurrent.Future

/**
  * Manager of AST states.
  */
trait IDialectManagerModule extends IServerIOCModule {

  /**
    * Module ID
    */
  val moduleId: String = IDialectManagerModule.moduleId

}


object IDialectManagerModule {
    val moduleId: String = "DIALECT_MANAGER"
}

