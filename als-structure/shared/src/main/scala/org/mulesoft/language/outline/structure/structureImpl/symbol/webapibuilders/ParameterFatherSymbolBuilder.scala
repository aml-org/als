package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.{AmfArray, AmfElement}
import amf.plugins.domain.webapi.metamodel.ParametersFieldModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.language.outline.structure.structureImpl._

class ArrayParametersSymbolBuilder(amfArray: AmfArray)(implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {
  override def build(): Seq[DocumentSymbol] = {
    val ranges = RangesSplitter(amfArray.annotations)
    new ParametersSymbolBuilder(amfArray.values.collect({ case p: Parameter => p }),
                                ranges.range,
                                ranges.selectionRange).build()
  }
}

object HeadersSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.Headers.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(element))
}

object QueryParametersSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.QueryParameters.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(element))
}

object QueryStringSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.QueryString.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(element))
}

object UriParametersSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.UriParameters.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new ArrayParametersSymbolBuilder(element))
}
