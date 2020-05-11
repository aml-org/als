package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.{KindForResultMatcher, SymbolKind}

trait FieldSymbolBuilder extends SymbolBuilder[FieldEntry] {
  override protected def kind: SymbolKind.SymbolKind = KindForResultMatcher.kindForField(element.field)
}

trait FieldSymbolBuilderCompanion extends SymbolBuilderCompanion[FieldEntry] {
  override def getType: Class[_] = classOf[FieldEntry]
}

trait IriFieldSymbolBuilderCompanion extends FieldSymbolBuilderCompanion with IriSymbolBuilderCompanion {}
