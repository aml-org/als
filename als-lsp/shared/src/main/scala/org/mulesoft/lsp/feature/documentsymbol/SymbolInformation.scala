package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.documentsymbol.SymbolKind.SymbolKind

/** Represents information about programming constructs like variables, classes, interfaces etc.
  *
  * @param name
  *   The name of this symbol.
  * @param kind
  *   The kind of this symbol.
  * @param location
  *   The location of this symbol. The location's range is used by a tool to reveal the location in the editor. If the
  *   symbol is selected in the tool the range's start information is used to position the cursor. So the range usually
  *   spans more then the actual symbol's name and does normally include things like visibility modifiers.
  *
  * The range doesn't have to denote a node range in the sense of a abstract syntax tree. It can therefore not be used
  * to re-construct a hierarchy of the symbols.
  * @param containerName
  *   The name of the symbol containing this symbol. This information is for user interface purposes (e.g. to render a
  *   qualifier in the user interface if necessary). It can't be used to re-infer a hierarchy for the document symbols.
  * @param deprecated
  *   Indicates if this symbol is deprecated.
  */
case class SymbolInformation(
    name: String,
    kind: SymbolKind,
    location: Location,
    containerName: Option[String],
    deprecated: Option[Boolean]
)
