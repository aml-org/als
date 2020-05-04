package org.mulesoft.language.outline.structure.structureImpl

import amf.core.model.domain._
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.{
  BaseUnitSymbolBuilderCompanion,
  DeclaresFieldSymbolBuilderCompanion,
  DomainElementSymbolBuilder,
  EncodesFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.DefaultArrayFieldTypeSymbolBuilderCompanion

trait BuilderFactory {
  implicit val factory: BuilderFactory = this

  def dialect: Dialect

  // separate to static in object for avoid recalculate all the list in AML factory instances
  protected def companion: FieldCompanionList =
    FieldCompanionList(
      List(DeclaresFieldSymbolBuilderCompanion,
           EncodesFieldSymbolBuilderCompanion,
           DefaultArrayFieldTypeSymbolBuilderCompanion),
      List(BaseUnitSymbolBuilderCompanion, DomainElementSymbolBuilder)
    )

  private lazy val companionList: FieldCompanionList = companion

  def builderFor(obj: AmfObject): Option[SymbolBuilder[_ <: AmfObject]] = companionList.find(obj)

  def builderFor(e: FieldEntry, location: Option[String]): Option[SymbolBuilder[FieldEntry]] = {
    if (location.forall(l => l == e.value.value.location().getOrElse(l)))
      companionList.find(e)
    else None
  }
}
