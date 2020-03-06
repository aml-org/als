package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.metamodel.Field
import amf.core.model.domain.AmfElement
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  ElementSymbolBuilder,
  ElementSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait

class ParameterSymbolBuilder(override val element: Parameter)(implicit val factory: BuilderFactory)
    extends NamedElementSymbolBuilderTrait[Parameter] {
  override def ignoreFields: List[Field] = super.ignoreFields :+ ParameterModel.Schema

  override protected def children: List[DocumentSymbol] =
    super.children ++ Option(element.schema)
      .flatMap(factory.builderForElement)
      .map(bs => bs.build().flatMap(_.children))
      .getOrElse(Nil)
}

object ParameterSymbolBuilderCompanion extends ElementSymbolBuilderCompanion {
  override type T = Parameter

  override def getType: Class[_ <: AmfElement] = classOf[Parameter]

  override val supportedIri: String = ParameterModel.`type`.head.iri()

  override def construct(element: Parameter)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[Parameter]] =
    Some(new ParameterSymbolBuilder(element))
}
