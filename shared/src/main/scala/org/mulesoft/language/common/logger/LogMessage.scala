package org.mulesoft.language.common.logger

trait LogMessage {
  var message: String
  var severity: MessageSeverity.Value
  var component: String
  var subcomponent: String
}
