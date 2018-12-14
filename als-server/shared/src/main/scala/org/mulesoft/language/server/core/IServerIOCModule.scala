package org.mulesoft.language.server.core

import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.core.platform.ConnectionBasedPlatform

/**
  * For modules that has this trait, the server engine initializes and
  * pushes dependency modules automatically.
  *
  * All possible pushes are guaranteed to be made before each launch
  */
trait IServerIOCModule extends IServerModule {

  /**
    * Pushes dependency to the module.
    * @param dependency - module, this module depends from.
    */
  def insertDependency(dependency: IServerModule)

  /**
    * Pushes server connection to the module
    * @param serverConnection
    */
  def insertConnection(serverConnection: IServerConnection)

  /**
    * Pushes platform dependency
    * @param platform
    */
  def insertPlatform(platform: ConnectionBasedPlatform)
}
