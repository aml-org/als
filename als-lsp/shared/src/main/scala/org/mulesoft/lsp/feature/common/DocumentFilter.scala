package org.mulesoft.lsp.feature.common

/** @param language
  *   A language id, like `typescript`.
  * @param scheme
  *   A Uri [scheme](#Uri.scheme), like `file` or `untitled`.
  * @param pattern
  *   A glob pattern, like `*.{ts,js}`. (more info in LSP spec)
  */
case class DocumentFilter(language: Option[String], scheme: Option[String], pattern: Option[String])
