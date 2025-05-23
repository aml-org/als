package org.mulesoft.als.suggestions.antlr.plugins.grpc.snippets

import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionItemKind, InsertTextFormat}

case class SnippetStructure (label: String, content: String, description: Option[String] = None) {
  def toCompletionItem: CompletionItem = CompletionItem(s"new $label",
    kind = Some(CompletionItemKind.Snippet),
    detail = description,
    sortText = Some(s"00$label"),
    insertText = Some(content),
    insertTextFormat = Some(InsertTextFormat.Snippet)
  )
}

object RootSnippets {
  def getAll: Seq[SnippetStructure] = Seq(
    SnippetStructure("syntax", "syntax = \"proto3\";\n$0", Some("defines a protobuf 3 document")),
    SnippetStructure("import", "import ${1| |weak |public |}\"$2\";\n$0"),
    SnippetStructure("option", "option ${1:option name} = ${2:option};\n$0"),
    SnippetStructure("package", "package ${1:package name};\n$0"),
    SnippetStructure("message", """message ${1:name} {
                                      |  $2
                                      |}$0""".stripMargin),
    SnippetStructure("enum", """enum ${1:name} {
                               |  $2
                               |}$0""".stripMargin)
  )
}