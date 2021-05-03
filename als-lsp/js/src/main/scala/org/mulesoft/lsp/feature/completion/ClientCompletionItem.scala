package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.edit.{ClientInsertReplaceEdit, ClientTextEdit}
import org.mulesoft.lsp.feature.command.ClientCommand

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

  def textEdit: js.UndefOr[ClientTextEdit | ClientInsertReplaceEdit] = js.native

  def additionalTextEdits: js.UndefOr[js.Array[ClientTextEdit]] = js.native

  def commitCharacters: js.UndefOr[js.Array[String]] = js.native

  def command: js.UndefOr[ClientCommand] = js.native
}

object ClientCompletionItem {
  def apply(internal: CompletionItem): ClientCompletionItem =
    js.Dynamic
      .literal(
        label = internal.label,
        kind = internal.kind.map(_.id).orUndefined,
        detail = internal.detail.orUndefined,
        documentation = internal.documentation.orUndefined,
        deprecated = internal.deprecated.orUndefined,
        preselect = internal.preselect.orUndefined,
        sortText = internal.sortText.orUndefined,
        filterText = internal.filterText.orUndefined,
        insertText = internal.insertText.orUndefined,
        insertTextFormat = internal.insertTextFormat.map(_.id).orUndefined,
        textEdit = internal.textEdit.map(_.toClient).orUndefined.asInstanceOf[js.Any],
        additionalTextEdits = internal.additionalTextEdits.map(a => a.map(_.toClient).toJSArray).orUndefined,
        commitCharacters = internal.commitCharacters.map(a => a.map(_.toString).toJSArray).orUndefined,
        command = internal.command.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientCompletionItem]
}

// $COVERAGE-ON$
