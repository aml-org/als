package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  FieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion,
  ScalarFieldTypeSymbolBuilder,
  ScalarFieldTypeSymbolBuilderCompanion
}

class DefaultWebApiScalarTypeSymbolBuilder(override val value: AmfScalar, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ScalarFieldTypeSymbolBuilder {

  private val mapName = Map(
    WebApiModel.Name    -> "title",
    WebApiModel.Version -> "version"
  )

  override protected val name: Option[String] = mapName.get(element.field)
}

object DefaultWebApiScalarTypeSymbolBuilderCompanion extends ScalarFieldTypeSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfScalar)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfScalar]] =
    Some(new DefaultWebApiScalarTypeSymbolBuilder(value, element))
}

class DefaultWebApiObjectTypeSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ObjectFieldTypeSymbolBuilder {

  private val mapName = Map(
    WebApiModel.License  -> "License",
    WebApiModel.Provider -> "Contact"
  )

  override protected val name: Option[String] = mapName.get(element.field)
}

object DefaultWebApiObjectTypeSymbolBuilderCompanion extends ObjectFieldTypeSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfObject)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new DefaultWebApiObjectTypeSymbolBuilder(value, element))
}
