package org.mulesoft.language.outline.structure.structureImpl

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

class StructureContext(val location: String, val factory: BuilderFactory, val documentDefinition: DocumentDefinition) {}

private class StructureContextBuilder(unit: BaseUnit) {
  private var factory: BuilderFactory = _
  private var d: DocumentDefinition              = _

  def withDefinition(d: DocumentDefinition): StructureContextBuilder = {
    this.d = d
    this
  }

  def withFactory(f: BuilderFactory): StructureContextBuilder = {
    factory = f
    this
  }

  def build(): StructureContext = new StructureContext(unit.identifier, factory, d)
}
