package org.mulesoft.lsp.textsync

case class DidChangeConfigurationNotificationParams(mainUri: String,
                                                    dependencies: Set[String],
                                                    customValidationProfiles: Set[String])
