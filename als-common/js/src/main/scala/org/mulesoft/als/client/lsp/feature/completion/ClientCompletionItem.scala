package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.als.client.lsp.command.ClientCommand
import org.mulesoft.lsp.edit.TextEdit

import scala.scalajs.js

@js.native
trait ClientCompletionItem extends js.Object {
  def label: String = js.native

  def kind: js.UndefOr[Int] = js.native

  def detail: js.UndefOr[String] = js.native

  def documentation: js.UndefOr[String] = js.native

  def deprecated: js.UndefOr[Boolean] = js.native

  def preselect: js.UndefOr[Boolean] = js.native

  def sortText: js.UndefOr[String] = js.native

  def filterText: js.UndefOr[String] = js.native

  def insertText: js.UndefOr[String] = js.native

  def insertTextFormat: js.UndefOr[Int] = js.native

  def textEdit: js.UndefOr[TextEdit] = js.native

  def additionalTextEdits: js.UndefOr[js.Array[TextEdit]] = js.native

  def commitCharacters: js.UndefOr[js.Array[Char]] = js.native

  def command: js.UndefOr[ClientCommand] = js.native
}
