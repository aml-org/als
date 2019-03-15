package org.mulesoft.language.server.core

import scala.concurrent.Future

/**
  * Abstract server module
  */
trait ServerModule {

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
  def launch(): Future[Unit]

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
