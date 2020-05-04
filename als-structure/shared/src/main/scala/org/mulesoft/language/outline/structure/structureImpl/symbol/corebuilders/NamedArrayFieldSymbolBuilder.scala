package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, AmfScalar}
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.{
  ArrayFieldTypeSymbolBuilder,
  DocumentSymbol,
  FieldSymbolBuilder,
  FieldTypeSymbolBuilder,
  KindForResultMatcher,
  ObjectFieldTypeSymbolBuilder,
  ScalarFieldTypeSymbolBuilder,
  SymbolKind
}

trait NamedFieldSymbolBuilder[ElementType <: AmfElement] extends FieldTypeSymbolBuilder[ElementType] {
  protected val name: String
  protected val kind: SymbolKind = KindForResultMatcher.kindForField(element.field)
  protected val children: List[DocumentSymbol]
  override def build(): Seq[DocumentSymbol] =
    Seq(DocumentSymbol(name, kind, deprecated = false, range, range, children))
}

trait NamedArrayFieldSymbolBuilder extends ArrayFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfArray] {

  override protected val children: List[DocumentSymbol] =
    value.values.collect({ case o: AmfObject => o }).flatMap(b => factory.builderFor(b)).flatMap(_.build()).toList
}

trait SingleObjectFieldSymbolBuilder extends ObjectFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfObject] {

  override protected val children: List[DocumentSymbol] = Nil
}

trait NamedScalarFieldTypeSymbolBuilder extends ScalarFieldTypeSymbolBuilder with NamedFieldSymbolBuilder[AmfScalar] {
  override protected val children: List[DocumentSymbol] = Nil
}
