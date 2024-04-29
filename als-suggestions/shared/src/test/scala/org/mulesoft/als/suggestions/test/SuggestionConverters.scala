package org.mulesoft.als.suggestions.test

import org.mulesoft.lsp.feature.command.Command
import org.mulesoft.lsp.feature.common.{Position, Range}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.completion.CompletionItem
import upickle.default.{macroRW, ReadWriter => RW}

case class CompletionPosition(line: Int, character: Int)

object CompletionPosition {

  implicit def rw: RW[CompletionPosition] = macroRW

  implicit def sharedToTransport(from: Position): CompletionPosition = {
    CompletionPosition(from.line, from.character)
  }
}

case class CompletionRange(start: CompletionPosition, end: CompletionPosition)

object CompletionRange {

  implicit def rw: RW[CompletionRange] = macroRW

  implicit def sharedToTransport(from: Range): CompletionRange =
    CompletionRange(CompletionPosition.sharedToTransport(from.start), CompletionPosition.sharedToTransport(from.end))
}

case class TextEditNode(range: CompletionRange, newText: String)

object TextEditNode {

  implicit def rw: RW[TextEditNode] = macroRW

  implicit def sharedToTransport(from: TextEdit): TextEditNode =
    TextEditNode(CompletionRange.sharedToTransport(from.range), from.newText)
}

case class CompletionCommand(title: String, command: String)

object CompletionCommand {

  implicit def rw: RW[CompletionCommand] = macroRW

  implicit def sharedToTransport(from: Command): CompletionCommand = {
    CompletionCommand(from.title, from.command)
  }
}

case class CompletionItemNode(
    label: String,
    kind: Option[Int],
    detail: String,
    documentation: String,
    deprecated: Option[Boolean],
    preselect: Option[Boolean],
    sortText: String,
    filterText: String,
    insertText: String,
    insertTextFormat: Option[Int],
    textEdit: TextEditNode,
    additionalTextEdits: Seq[TextEditNode],
    commitCharacters: Seq[Char],
    command: CompletionCommand
)

object CompletionItemNode {

  implicit def rw: RW[CompletionItemNode] = macroRW

  implicit def sharedToTransport(from: CompletionItem): CompletionItemNode = {

    CompletionItemNode(
      from.label,
      from.kind.map(_.id),
      from.detail.orNull,
      from.documentation.orNull,
      from.deprecated,
      from.preselect,
      from.sortText.orNull,
      from.filterText.orNull,
      from.insertText.orNull,
      from.insertTextFormat.map(_.id),
      from.textEdit.flatMap(t => t.left.toOption).map(t => TextEditNode.sharedToTransport(t)).orNull,
      from.additionalTextEdits.map(_.map(t => TextEditNode.sharedToTransport(t))).orNull,
      from.commitCharacters.orNull,
      from.command.map(c => CompletionCommand.sharedToTransport(c)).orNull
    )
  }
}
