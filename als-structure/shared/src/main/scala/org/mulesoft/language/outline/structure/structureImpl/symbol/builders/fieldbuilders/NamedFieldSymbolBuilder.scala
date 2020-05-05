package org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders

import amf.core.model.domain.AmfElement
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher}

trait NamedFieldSymbolBuilder[ElementType <: AmfElement] extends FieldTypeSymbolBuilder[ElementType] {
  protected def name: String
  protected val kind: SymbolKind = KindForResultMatcher.kindForField(element.field)
  protected val children: List[DocumentSymbol]
  override def build(): Seq[DocumentSymbol] =
    Seq(DocumentSymbol(name, kind, deprecated = false, range, range, children))
}
