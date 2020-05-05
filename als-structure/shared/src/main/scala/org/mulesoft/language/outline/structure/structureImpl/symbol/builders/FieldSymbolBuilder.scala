package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.parser.FieldEntry

trait FieldSymbolBuilder extends SymbolBuilder[FieldEntry] {}

trait FieldSymbolBuilderCompanion extends SymbolBuilderCompanion[FieldEntry] {
  override def getType: Class[_] = classOf[FieldEntry]
}

trait IriFieldSymbolBuilderCompanion extends FieldSymbolBuilderCompanion with IriSymbolBuilderCompanion {}
