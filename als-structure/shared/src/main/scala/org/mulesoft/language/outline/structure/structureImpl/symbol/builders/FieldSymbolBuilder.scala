package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.{KindForResultMatcher, SymbolKinds}

trait FieldSymbolBuilder extends SymbolBuilder[FieldEntry] {
  override protected def kind: SymbolKinds.SymbolKind = KindForResultMatcher.kindForField(element.field)
}

trait FieldSymbolBuilderCompanion extends SymbolBuilderCompanion[FieldEntry] {
  override def getType: Class[_] = classOf[FieldEntry]
}

trait IriFieldSymbolBuilderCompanion extends FieldSymbolBuilderCompanion with IriSymbolBuilderCompanion {}
