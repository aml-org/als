package org.mulesoft.language.server.core

import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.core.platform.ConnectionBasedPlatform

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

/**
  * Abstract server module handling dependencies and launching.
  */
abstract class AbstractServerModule extends IServerIOCModule {

  /**
    * Initialized and pushed dependencies.
    */
  protected val initializedDependencies: mutable.Buffer[IServerModule] = ArrayBuffer()

  /**
    * Server connection. As its basically guaranteed to not to be null during any
    * real code execution, avoiding optional to shorten the code.
    */
  protected var connection: IServerConnection = _

  /**
    * Platform.
    */
  protected var platform: ConnectionBasedPlatform = _

  /**
    * Whether module is launched
    */
  protected var launched = false

  def getDependencyById[T <: IServerModule](moduleId: String): Option[T] = {

    val moduleOption = this.initializedDependencies.find(dependencyModule => dependencyModule.moduleId == moduleId)

    moduleOption.map(module => module.asInstanceOf[T])
  }

  //  def dependencyByInterface[T <: IServerModule](interfaceName: String): Option[T] = {
  //
  //    val moduleOption = this.initializedDependencies.find(
  //      dependencyModule => {
  //        dependencyModule.isInstanceOf[IServerIOCModule] &&
  //          dependencyModule.asInstanceOf[IServerIOCModule].mainInterfaceName.isDefined &&
  //          dependencyModule.asInstanceOf[IServerIOCModule].mainInterfaceName.get == interfaceName
  //      })
  //
  //    moduleOption.map(module=>module.asInstanceOf[T])
  //  }

  /**
    * Launches module. Either returns this or launch failure reason.
    *
    * Intended to be called from subtype to check that all dependencies are in place
    *
    * @return
    */
  override def launch(): Try[IServerModule] = {

    val result = this.checkDependencies()
    this.launched = true

    result
  }

  /**
    * Stops the module.
    */
  override def stop(): Unit = {

    this.launched = false
  }

  /**
    * Checks whether this module is launched.
    *
    * @return
    */
  override def isLaunched(): Boolean = {
    this.launched;
  }

  /**
    * Pushes dependency to the module.
    *
    * @param dependency - module, this module depends from.
    */
  override def insertDependency(dependency: IServerModule): Unit = {

    this.initializedDependencies += dependency
  }

  /**
    * Pushes server connection to the module
    *
    * @param serverConnection
    */
  override def insertConnection(serverConnection: IServerConnection): Unit = {

    this.connection = serverConnection
  }

  /**
    * Pushes platform dependency
    *
    * @param platform
    */
  override def insertPlatform(platform: ConnectionBasedPlatform) = {

    this.platform = platform
  }

  protected def checkDependencies(): Try[IServerModule] = {
    val unsatisfied = this.moduleDependencies.filter(dependencyId => {

      val found = this.initializedDependencies.find(dependency => {
        dependency.moduleId == dependencyId
      })

      found.isEmpty
    })

    if (unsatisfied.length > 0) {

      Failure(
        new Exception(
          "Following dependencies are not found: " +
            unsatisfied.mkString(",")))

    } else {

      Success(this)
    }
  }

}
