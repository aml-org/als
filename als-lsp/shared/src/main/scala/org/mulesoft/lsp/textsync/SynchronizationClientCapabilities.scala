package org.mulesoft.lsp.textsync

/** @param dynamicRegistration
  *   Whether text document synchronization supports dynamic registration.
  * @param willSave
  *   The client supports sending will save notifications.
  * @param willSaveWaitUntil
  *   The client supports sending a will save request and waits for a response providing text edits which will be
  *   applied to the document before it is saved.
  * @param didSave
  *   The client supports did save notifications.
  */

case class SynchronizationClientCapabilities(
    dynamicRegistration: Option[Boolean],
    willSave: Option[Boolean],
    willSaveWaitUntil: Option[Boolean],
    didSave: Option[Boolean]
)
