package org.mulesoft.lsp.configuration

case class WorkspaceFolderServerCapabilities(supported: Option[Boolean], changeNotifications: Option[Either[String, Boolean]])
