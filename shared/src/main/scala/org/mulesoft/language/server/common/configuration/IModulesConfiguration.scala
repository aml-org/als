package org.mulesoft.language.server.common.configuration

trait IModulesConfiguration {
  var enableDetailsModule: Boolean
  var enableCustomActionsModule: Boolean
  var enableASTManagerModule: Boolean
  var enableCompletionManagerModule: Boolean
  var enableEditorManagerModule: Boolean
  var enableFixedActionsModule: Boolean
  var enableStructureManagerModule: Boolean
  var enableValidationManagerModule: Boolean
  var allModules: Boolean
}
