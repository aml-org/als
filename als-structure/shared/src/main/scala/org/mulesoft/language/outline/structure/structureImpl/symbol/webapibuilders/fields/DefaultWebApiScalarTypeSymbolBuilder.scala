package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.{NamedScalarFieldTypeSymbolBuilder, SingleObjectFieldSymbolBuilder}

class DefaultWebApiScalarTypeSymbolBuilder(override val value: AmfScalar, override val element: FieldEntry, override val name :String)(
    override implicit val factory: BuilderFactory)
    extends NamedScalarFieldTypeSymbolBuilder {
}

object DefaultWebApiScalarTypeSymbolBuilderCompanion extends ScalarFieldTypeSymbolBuilderCompanion {

  private val mapName = Map(
    WebApiModel.Name    -> "title",
    WebApiModel.Version -> "version"
  )

  override def construct(element: FieldEntry, value: AmfScalar)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfScalar]] =
    mapName.get(element.field).map(name => new DefaultWebApiScalarTypeSymbolBuilder(value, element, name))
}


class DefaultWebApiObjectTypeSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends SingleObjectFieldSymbolBuilder {

  // Linceese and contant are named domain eelemento, so i cannot apply this hack
  private val mapName = Map(
    WebApiModel.License  -> "License",
    WebApiModel.Provider -> "Contact"
  )

  override protected val name: String = mapName.getOrElse(element.field, element.field.value.name)
}

object DefaultWebApiObjectTypeSymbolBuilderCompanion extends ObjectFieldTypeSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfObject)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new DefaultWebApiObjectTypeSymbolBuilder(value, element))
}
