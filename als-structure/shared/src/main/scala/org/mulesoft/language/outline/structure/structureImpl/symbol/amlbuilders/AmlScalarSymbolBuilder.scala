package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.model.domain.AmfScalar
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.WebApiScalarBuilder

case class AmlScalarSymbolBuilder(override val name: String, override val scalar: AmfScalar)(
    override implicit val factory: BuilderFactory)
    extends WebApiScalarBuilder
