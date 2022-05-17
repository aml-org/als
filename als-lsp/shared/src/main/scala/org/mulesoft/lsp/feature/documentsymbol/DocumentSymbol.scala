package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind
import org.mulesoft.lsp.feature.common.Range

/** Represents programming constructs like variables, classes, interfaces etc. that appear in a document. Document
  * symbols can be hierarchical and they have two ranges: one that encloses its definition and one that points to its
  * most interesting range, e.g. the range of an identifier.
  *
  * @param name
  *   The name of this symbol. Will be displayed in the user interface and therefore must not be an empty string or a
  *   string only consisting of white spaces.
  * @param kind
  *   The kind of this symbol.
  * @param range
  *   The range enclosing this symbol not including leading/trailing whitespace but everything else like comments. This
  *   information is typically used to determine if the clients cursor is inside the symbol to reveal in the symbol in
  *   the UI.
  * @param selectionRange
  *   The range that should be selected and revealed when this symbol is being picked, e.g the name of a function. Must
  *   be contained by the `range`.
  * @param children
  *   Children of this symbol, e.g. properties of a class.
  * @param detail
  *   More detail for this symbol, e.g the signature of a function.
  * @param deprecated
  *   The kind of this symbol.
  */
case class DocumentSymbol(
    name: String,
    kind: SymbolKind,
    range: Range,
    selectionRange: Range,
    children: Seq[DocumentSymbol] = Seq(),
    detail: Option[String] = None,
    deprecated: Option[Boolean] = None
)
