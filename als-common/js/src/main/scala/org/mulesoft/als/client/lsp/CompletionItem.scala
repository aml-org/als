package org.mulesoft.als.client.lsp

import org.mulesoft.lsp
import org.mulesoft.als.client.convert.LspConverters._
import org.mulesoft.lsp.edit.TextEdit

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "CompletionItem")
class CompletionItem(private val internal: lsp.feature.completion.CompletionItem) {
  def label: String = internal.label

  def kind: js.UndefOr[Int] = internal.kind.map(_.id).orUndefined

  def detail: js.UndefOr[String] = internal.detail.orUndefined

  def documentation: js.UndefOr[String] = internal.documentation.orUndefined

  def deprecated: js.UndefOr[Boolean] = internal.deprecated.orUndefined

  def preselect: js.UndefOr[Boolean] = internal.preselect.orUndefined

  def sortText: js.UndefOr[String] = internal.sortText.orUndefined

  def filterText: js.UndefOr[String] = internal.filterText.orUndefined

  def insertText: js.UndefOr[String] = internal.insertText.orUndefined

  def insertTextFormat: js.UndefOr[Int] = internal.insertTextFormat.map(_.id).orUndefined

  def textEdit: js.UndefOr[TextEdit] = internal.textEdit.orUndefined

  def additionalTextEdits: js.UndefOr[js.Array[TextEdit]] = internal.additionalTextEdits.map(_.toJSArray).orUndefined

  def commitCharacters: js.UndefOr[js.Array[Char]] = internal.commitCharacters.map(_.toJSArray).orUndefined

  def command: js.UndefOr[Command] = internal.command.map(toClientCommand).orUndefined
}
