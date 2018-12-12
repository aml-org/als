package org.mulesoft.language.common.dtoTypes

trait IUIDisplayRequest {
  var action: IExecutableAction
  var uiCode: String
  var initialUIState: Any
}
