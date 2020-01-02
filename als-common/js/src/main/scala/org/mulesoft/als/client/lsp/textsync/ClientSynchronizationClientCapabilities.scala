package org.mulesoft.als.client.lsp.textsync

import org.mulesoft.lsp.textsync.SynchronizationClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr

@js.native
trait ClientSynchronizationClientCapabilities extends js.Object {
  def dynamicRegistration: UndefOr[Boolean] = js.native
  def willSave: UndefOr[Boolean]            = js.native
  def willSaveWaitUntil: UndefOr[Boolean]   = js.native
  def didSave: UndefOr[Boolean]             = js.native
}

object ClientSynchronizationClientCapabilities {
  def apply(internal: SynchronizationClientCapabilities): ClientSynchronizationClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        willSave = internal.willSave.orUndefined,
        willSaveWaitUntil = internal.willSaveWaitUntil.orUndefined,
        didSave = internal.didSave.orUndefined
      )
      .asInstanceOf[ClientSynchronizationClientCapabilities]
}
