package org.mulesoft.language.server.common.configuration

trait IServerConfiguration {
  var actionsConfiguration: IActionsConfiguration
  var modulesConfiguration: IModulesConfiguration
}
