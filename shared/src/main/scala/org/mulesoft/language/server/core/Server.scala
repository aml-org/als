package org.mulesoft.language.server.core

import org.mulesoft.language.server.core.connections.IServerConnection

import scala.collection.mutable


class Server(val connection: IServerConnection) {

  var modules: mutable.Map[String, IServerModule] = new mutable.HashMap()


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

      this.enableModuleDependencies(module)

      if (module.isInstanceOf[IServerIOCModule]) {
        this.pushModuleDependencies(module.asInstanceOf[IServerIOCModule])
      }

    } else {

      connection.warning("Cant enable module " + moduleName + " as its not found",
        "server", "enableModule")
    }

  }

  def enableModuleDependencies(module: IServerModule): Unit = {
    module.moduleDependencies.foreach(depId=>{
      val moduleOption = modules.get(depId)
      if (moduleOption.isDefined) {
        this.enableModule(depId)
      } else {
        connection.warning("Cant find dependency " + depId,
          "server", "enableModuleDependencies")
      }
    })
  }

  def pushModuleDependencies(module: IServerIOCModule): Unit = {

    module.insertConnection(this.connection)

    module.moduleDependencies.foreach(depId=>{

      val moduleOption = modules.get(depId)

      if (moduleOption.isDefined) {

        module.insertDependency(moduleOption.get)
      } else {
        connection.warning("Cant find dependency " + depId,
          "server", "enableModuleDependencies")
      }
    })
  }

  def disableModule(moduleName: String): Unit = {
    this.connection.debugDetail("Changing module enablement of " + moduleName + " to false",
      "server", "onSetServerConfiguration")

    val moduleOption = modules.get(moduleName)
    if (moduleOption.isDefined) {
      val module = moduleOption.get

      module.stop()

    } else {

      connection.warning("Cant enable module " + moduleName + " as its not found",
        "server", "enableModule")
    }
  }


}
