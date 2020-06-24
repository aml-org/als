package org.mulesoft.language.outline.structure.structureImpl

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp

class StructureContext(val location: String, val factory: BuilderFactory, val dialect: Dialect) {}

private class StructureContextBuilder(unit: BaseUnit) {
  private var factory: BuilderFactory = _
  private var d: Dialect              = _

  def withDialect(d: Dialect): StructureContextBuilder = {
    this.d = d
    this
  }

  def withFactory(f: BuilderFactory): StructureContextBuilder = {
    factory = f
    this
  }

  def build(): StructureContext = new StructureContext(unit.identifier, factory, d)
}
