package org.mulesoft.language.server.modules.dialectManager

import org.mulesoft.language.server.core.IServerIOCModule

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

