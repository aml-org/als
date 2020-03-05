package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.core.metamodel.Obj
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.model.document.BaseUnit
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.BaseUnitSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, ElementSymbolBuilder}

class RamlBaseUnitSymbolBuilder(element: BaseUnit)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(element) {

  override protected def nameFromMeta(obj: Obj): String = obj match {
    case _: ShapeModel             => "types"
    case ResourceTypeModel         => "resourceTypes"
    case TraitModel                => "traits"
    case SecuritySchemeModel       => "securitySchemes"
    case CustomDomainPropertyModel => "annotationTypes"
    case _                         => "unknowns"
  }
}

object RamlBaseUnitSymbolBuilder extends BaseUnitSymbolBuilderCompanion {

  override def construct(element: T)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[BaseUnit]] =
    Some(new RamlBaseUnitSymbolBuilder(element))
}
