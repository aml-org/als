package org.mulesoft.language.server.core

import org.mulesoft.language.server.core.connections.ServerConnection
import org.mulesoft.language.server.core.platform.{ConnectionBasedPlatform, PlatformDependentPart}
import org.mulesoft.language.server.modules.editorManager.{EditorManager, EditorManagerModule}

import scala.collection.mutable
import scala.concurrent.Future

class Server(val connection: ServerConnection, protected val httpFetcher: PlatformDependentPart) {

  var modules: mutable.Map[String, ServerModule] = new mutable.HashMap()

  protected val platform: ConnectionBasedPlatform = this.constructPlatform

  protected def constructPlatform: ConnectionBasedPlatform = {

    val editorManager = new EditorManager()

    this.registerModule(editorManager)
    this.enableModule(EditorManagerModule.moduleId)

    val httpFetcher = this.httpFetcher

    new ConnectionBasedPlatform(this.connection, editorManager, httpFetcher)
  }

  def registerModule(module: ServerModule): Unit = {
    val moduleName = module.moduleId

    modules(moduleName) = module
  }

  def enableModule(moduleName: String): Future[Unit] = {
    this.connection.debugDetail("Changing module enablement of " + moduleName + " to true", "server", "enableModule")

    val moduleOption = modules.get(moduleName)
    if (moduleOption.isDefined) {
      val module = moduleOption.get

      this.connection.debugDetail("Starting to Enable module dependencies " + moduleName, "server", "enableModule")
      this.enableModuleDependencies(module)

      this.connection.debugDetail("Done enabling module dependencies " + moduleName, "server", "enableModule")

      if (module.isInstanceOf[ServerIOCModule]) {

        this.pushModuleDependencies(module.asInstanceOf[ServerIOCModule])
      }

      if (!module.isLaunched) {
        module.launch()
      } else {
        Future.successful()
      }
    } else {
      connection.warning("Cant enable module " + moduleName + " as its not found", "server", "enableModule")

      Future.successful()
    }
  }

  def enableModuleDependencies(module: ServerModule): Unit = {
    module.moduleDependencies.foreach(depId => {
      val moduleOption = modules.get(depId)
      if (moduleOption.isDefined) {
        this.enableModule(depId)
      } else {
        connection.warning("Cant find dependency " + depId, "server", "enableModuleDependencies")
      }
    })
  }

  def pushModuleDependencies(module: ServerIOCModule): Unit = {

    this.connection
      .debugDetail("Starting to push module dependencies " + module.moduleId, "server", "pushModuleDependencies")

    module.insertConnection(this.connection)

    module.insertPlatform(this.platform)

    module.moduleDependencies.foreach(depId => {

      val moduleOption = modules.get(depId)

      if (moduleOption.isDefined) {

        module.insertDependency(moduleOption.get)
      } else {
        connection.warning("Cant find dependency " + depId, "server", "enableModuleDependencies")
      }
    })

    this.connection
      .debugDetail("Finished to push module dependencies " + module.moduleId, "server", "pushModuleDependencies")
  }

  def disableModule(moduleName: String): Unit = {
    this.connection
      .debugDetail("Changing module enablement of " + moduleName + " to false", "server", "onSetServerConfiguration")

    val moduleOption = modules.get(moduleName)
    if (moduleOption.isDefined) {
      val module = moduleOption.get

      module.stop()

    } else {
      connection.warning("Cant enable module " + moduleName + " as its not found", "server", "enableModule")
    }
  }

}
