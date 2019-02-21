package org.mulesoft.language.server.core

import org.mulesoft.language.server.core.connections.ServerConnection
import org.mulesoft.language.server.core.platform.ConnectionBasedPlatform

/**
  * For modules that has this trait, the server engine initializes and
  * pushes dependency modules automatically.
  *
  * All possible pushes are guaranteed to be made before each launch
  */
trait ServerIOCModule extends ServerModule {

  /**
    * Pushes dependency to the module.
    *
    * @param dependency - module, this module depends from.
    */
  def insertDependency(dependency: ServerModule)

  /**
    * Pushes server connection to the module
    *
    * @param serverConnection
    */
  def insertConnection(serverConnection: ServerConnection)

  /**
    * Pushes platform dependency
    *
    * @param platform
    */
  def insertPlatform(platform: ConnectionBasedPlatform)
}
