package org.mulesoft.language.outline.structure.structureImpl

import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.SymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._

trait BuilderFactory {

  // separate to static in object for avoid recalculate all the list in AML factory instances
  protected def companion: FieldCompanionList =
    FieldCompanionList(
      List(
        DeclaresFieldSymbolBuilderCompanion,
        EncodesFieldSymbolBuilderCompanion,
        DefaultArrayFieldTypeSymbolBuilderCompanion,
        ReferencesFieldSymbolBuilderCompanion
      ),
      List(BaseUnitSymbolBuilderCompanion, DomainElementSymbolBuilder)
    )

  private lazy val companionList: FieldCompanionList = companion

  def builderFor(obj: AmfObject)(implicit ctx: StructureContext): Option[SymbolBuilder[_ <: AmfObject]] = {
    if (obj.location().forall(l => l == ctx.location))
      companionList.find(obj)
    else None
  }

  def builderFor(e: FieldEntry)(implicit ctx: StructureContext): Option[SymbolBuilder[FieldEntry]] = {
    if (e.value.value.location().forall(l => l == ctx.location))
      companionList.find(e)
    else None
  }
}
