package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.metamodel.Obj
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.model.document.BaseUnit
import amf.plugins.domain.webapi.metamodel.{OperationModel, ParameterModel, PayloadModel, ResponseModel}
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, ElementSymbolBuilder}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.BaseUnitSymbolBuilderCompanion

case class OasBaseUnitSymbolBuilder(bu: BaseUnit)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(bu) {
  override protected def nameFromMeta(meta: Obj): String = {
    meta match {
      case _: ShapeModel                 => "schemes"
      case ResourceTypeModel             => "x-amf-resourceTypes"
      case ResponseModel                 => "responses"
      case ParameterModel | PayloadModel => "parameters"
      case TraitModel                    => "x-amf-trait"
      case SecuritySchemeModel           => "securityDefinitions"
      case CustomDomainPropertyModel     => "x-amf-annotationTypes"
      case _                             => "unknowns"
    }
  }
}

object OasBaseUnitSymbolBuilder extends BaseUnitSymbolBuilderCompanion {
  override def construct(element: BaseUnit)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[BaseUnit]] =
    Some(new OasBaseUnitSymbolBuilder(element))
}
