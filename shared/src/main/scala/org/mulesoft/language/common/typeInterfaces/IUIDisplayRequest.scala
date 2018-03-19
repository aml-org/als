package org.mulesoft.language.common.typeInterfaces

trait IUIDisplayRequest {
  var action: IExecutableAction
  var uiCode: String
  var initialUIState: Any
}
