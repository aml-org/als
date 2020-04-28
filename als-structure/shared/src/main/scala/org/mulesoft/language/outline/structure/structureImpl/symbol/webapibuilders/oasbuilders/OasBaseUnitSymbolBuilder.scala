package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders

import amf.core.metamodel.Obj
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, ElementSymbolBuilder}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.BaseUnitSymbolBuilderCompanion

case class Oas20BaseUnitSymbolBuilder(bu: BaseUnit)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(bu) {
  override protected def nameFromMeta(meta: Obj): String = {
    meta match {
      case _: ShapeModel                 => "definitions"
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

object Oas20BaseUnitSymbolBuilder extends BaseUnitSymbolBuilderCompanion {
  override def construct(element: BaseUnit)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[BaseUnit]] =
    Some(new Oas20BaseUnitSymbolBuilder(element))
}

case class Oas30BaseUnitSymbolBuilder(bu: BaseUnit)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(bu) {
  override protected def nameForDeclared(element: AmfObject): String = {
    element match {
      case p: Parameter if p.binding.option().contains("header") => "headers"
      case _                                                     => super.nameForDeclared(element)
    }
  }

  override protected def nameFromMeta(meta: Obj): String = {
    meta match {
      case _: ShapeModel                 => "schemas"
      case ResourceTypeModel             => "x-amf-resourceTypes"
      case ResponseModel                 => "responses"
      case ParameterModel | PayloadModel => "parameters"
      case TraitModel                    => "x-amf-trait"
      case SecuritySchemeModel           => "securityDefinitions"
      case CustomDomainPropertyModel     => "x-amf-annotationTypes"
      case TemplatedLinkModel            => "links"
      case ExampleModel                  => "examples"
      case CallbackModel                 => "callbacks"
      case RequestModel                  => "requestBodies"
      case _                             => "unknowns"
    }
  }
}

object Oas30BaseUnitSymbolBuilder extends BaseUnitSymbolBuilderCompanion {
  override def construct(element: BaseUnit)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[BaseUnit]] =
    Some(new Oas30BaseUnitSymbolBuilder(element))
}
