package org.mulesoft.language.server.core

import amf.client.remote.Content
import org.mulesoft.language.server.core.connections.IServerConnection
import org.mulesoft.language.server.core.platform.{ConnectionBasedPlatform, PlatformDependentPart}
import org.mulesoft.language.server.server.modules.editorManager.{EditorManager, IEditorManagerModule}

import scala.collection.mutable
import scala.concurrent.Future


class Server(val connection: IServerConnection,
             protected val httpFetcher: PlatformDependentPart) {

  var modules: mutable.Map[String, IServerModule] = new mutable.HashMap()

  protected val platform = this.constructPlatform

  protected def constructPlatform = {

    val editorManager = new EditorManager()

    this.registerModule(editorManager)
    this.enableModule(IEditorManagerModule.moduleId)

    val httpFetcher = this.httpFetcher

    new ConnectionBasedPlatform(this.connection, editorManager, httpFetcher)
  }

  def registerModule(module: IServerModule): Unit = {
    val moduleName = module.moduleId

    modules(moduleName) = module;
  }

  def enableModule(moduleName: String): Unit = {
    this.connection.debugDetail("Changing module enablement of " + moduleName + " to true",
      "server", "enableModule")

    val moduleOption = modules.get(moduleName)
    if (moduleOption.isDefined) {
      val module = moduleOption.get

      this.connection.debugDetail("Starting to Enable module dependencies " + moduleName,
        "server", "enableModule")
      this.enableModuleDependencies(module)

      this.connection.debugDetail("Done enabling module dependencies " + moduleName,
        "server", "enableModule")

      if (module.isInstanceOf[IServerIOCModule]) {

        this.pushModuleDependencies(module.asInstanceOf[IServerIOCModule])
      }

      if(!module.isLaunched()) {
        module.launch();
      }

    } else {
        // $COVERAGE-OFF$
      connection.warning("Cant enable module " + moduleName + " as its not found",
        "server", "enableModule")
        // $COVERAGE-ON$
    }

  }

  def enableModuleDependencies(module: IServerModule): Unit = {
    module.moduleDependencies.foreach(depId=>{
      val moduleOption = modules.get(depId)
      if (moduleOption.isDefined) {
        this.enableModule(depId)
      } else {
          // $COVERAGE-OFF$
        connection.warning("Cant find dependency " + depId,
          "server", "enableModuleDependencies")
          // $COVERAGE-ON$
      }
    })
  }

  def pushModuleDependencies(module: IServerIOCModule): Unit = {

    this.connection.debugDetail("Starting to push module dependencies " + module.moduleId,
      "server", "pushModuleDependencies")

    module.insertConnection(this.connection)

    module.insertPlatform(this.platform)

    module.moduleDependencies.foreach(depId=>{

      val moduleOption = modules.get(depId)

      if (moduleOption.isDefined) {

        module.insertDependency(moduleOption.get)
      } else {
          // $COVERAGE-OFF$
        connection.warning("Cant find dependency " + depId,
          "server", "enableModuleDependencies")
          // $COVERAGE-ON$
      }
    })

    this.connection.debugDetail("Finished to push module dependencies " + module.moduleId,
      "server", "pushModuleDependencies")
  }

  def disableModule(moduleName: String): Unit = {
    this.connection.debugDetail("Changing module enablement of " + moduleName + " to false",
      "server", "onSetServerConfiguration")

    val moduleOption = modules.get(moduleName)
    if (moduleOption.isDefined) {
      val module = moduleOption.get

      module.stop()

    } else {
        // $COVERAGE-OFF$
      connection.warning("Cant enable module " + moduleName + " as its not found",
        "server", "enableModule")
        // $COVERAGE-ON$
    }
  }


}
