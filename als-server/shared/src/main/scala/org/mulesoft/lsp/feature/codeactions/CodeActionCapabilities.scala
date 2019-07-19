package org.mulesoft.lsp.feature.codeactions

case class CodeActionCapabilities(dynamicRegistration: Option[Boolean], codeActionLiteralSupport: CodeActionLiteralSupportCapabilities)

