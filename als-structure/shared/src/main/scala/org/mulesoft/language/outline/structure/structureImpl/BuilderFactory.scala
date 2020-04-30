package org.mulesoft.language.outline.structure.structureImpl

import amf.core.model.domain._
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DomainElementSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.NameFieldSymbolBuilder

trait BuilderFactory {
  implicit val factory: BuilderFactory = this

  val dialect: Dialect
  protected def companion: FieldCompanionList =
    FieldCompanionList(Nil, List(DomainElementSymbolBuilder, NameFieldSymbolBuilder))

  private lazy val companionList: FieldCompanionList = companion

  def builderFor[T <: AmfObject](obj: T): Option[SymbolBuilder[_ <: AmfObject]] = companionList.find(obj)

  def builderFor(e: FieldEntry, location: Option[String]): Option[SymbolBuilder[FieldEntry]] = {
    if (location.forall(l => l == e.value.value.location().getOrElse(l)))
      companionList.find(e)
    else None
  }
}
