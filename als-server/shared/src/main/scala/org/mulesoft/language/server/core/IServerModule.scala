package org.mulesoft.language.server.core

import scala.util.Try

/**
  * Abstract server module
  */
trait IServerModule {

  /**
    * Module identifier
    */
  val moduleId: String

  /**
    * List of module dependencies.
    */
  val moduleDependencies: Array[String]

  /**
    * Launches module. Either returns this or launch failure reason.
    *
    * @return
    */
  def launch(): Try[IServerModule]

  /**
    * Stops the module.
    */
  def stop()

  /**
    * Checks whether this module is launched.
    *
    * @return
    */
  def isLaunched: Boolean
}
