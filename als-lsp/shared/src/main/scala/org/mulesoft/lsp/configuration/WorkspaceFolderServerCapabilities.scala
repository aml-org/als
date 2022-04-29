package org.mulesoft.lsp.configuration

case class WorkspaceFolderServerCapabilities(
    supported: Option[Boolean],
    changeNotifications: Option[Either[String, Boolean]]
) {

  def withSupported(value: Boolean): WorkspaceFolderServerCapabilities =
    WorkspaceFolderServerCapabilities(Some(value), changeNotifications)

  def withChangeNotification(value: Either[String, Boolean]): WorkspaceFolderServerCapabilities =
    WorkspaceFolderServerCapabilities(supported, Some(value))

}
