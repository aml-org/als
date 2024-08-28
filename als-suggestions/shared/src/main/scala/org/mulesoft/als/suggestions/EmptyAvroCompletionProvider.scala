package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.interfaces.CompletionProvider
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.common.{Position, Range}
import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionItemKind, InsertTextFormat}

import scala.concurrent.Future

object EmptyAvroCompletionProvider {
  def build(uri: String, endPosition: Position): CompletionProvider =
    new CompletionProvider{
      override def suggest(): Future[Seq[CompletionItem]] =
        Future.successful(Seq(
          CompletionItem("new Avro Schema", Some(CompletionItemKind.Snippet), Some("root"),
            textEdit = Some(Left(TextEdit(Range(Position(0, 0), endPosition), getText(uri)))),
            insertTextFormat = Some(InsertTextFormat.Snippet))
        ))

      private def getText(uri: String) =
        s"""{
          |    "name": "$${1:${getName(uri)}}",
          |    "type": "$${2:record}",
          |    $$0
          |}""".stripMargin

      private def getName(uri: String) =
        uri.split("/").lastOption.flatMap(_.split('.').headOption).getOrElse("myAvro")
    }
}
