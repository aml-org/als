package org.mulesoft.lsp.textsync

case class DidChangeConfigurationNotificationParams(mainUri: String,
                                                    folder: Option[String],
                                                    dependencies: Set[String],
                                                    customValidationProfiles: Set[String],
                                                    semanticExtensions: Set[String])
