package org.mulesoft.language.server.modules.dialectManager

import org.mulesoft.language.server.core.ServerIOCModule

/**
  * Manager of AST states.
  */
trait DialectManagerModule extends ServerIOCModule {

  /**
    * Module ID
    */
  val moduleId: String = DialectManagerModule.moduleId

}


object DialectManagerModule {
  val moduleId: String = "DIALECT_MANAGER"
}

