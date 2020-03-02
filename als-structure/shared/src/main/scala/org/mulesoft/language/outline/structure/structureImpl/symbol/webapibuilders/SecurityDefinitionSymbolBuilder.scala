package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.AmfElement
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.models.security.SecurityScheme
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  ElementSymbolBuilderCompanion
}

class SecurityDefinitionSymbolBuilder(override val element: SecurityScheme)(
    override implicit val factory: BuilderFactory)
    extends NamedElementSymbolBuilderTrait[SecurityScheme] {
  override protected def children: List[DocumentSymbol] = Nil
}

object SecurityDefinitionSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = SecurityScheme

  override def getType: Class[_ <: AmfElement] = classOf[SecurityScheme]

  override val supportedIri: String = SecuritySchemeModel.`type`.head.iri()

  override def construct(element: SecurityScheme)(
      implicit factory: BuilderFactory): Option[SecurityDefinitionSymbolBuilder] =
    Some(new SecurityDefinitionSymbolBuilder(element))
}
