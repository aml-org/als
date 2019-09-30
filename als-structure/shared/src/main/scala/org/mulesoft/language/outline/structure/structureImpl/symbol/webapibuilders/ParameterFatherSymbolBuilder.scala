package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.{AmfArray, AmfElement}
import amf.plugins.domain.webapi.metamodel.ParametersFieldModel
import org.mulesoft.language.outline.structure.structureImpl._

trait ParametersSymbolBuilder extends ElementSymbolBuilder[AmfArray] {
  val element: AmfArray
  protected val name: String
  val ranges = RangesSplitter(element.annotations)
  val children: Seq[DocumentSymbol] =
    element.values.flatMap(e => factory.builderForElement(e).map(_.build()).getOrElse(Nil))

  override def build(): Seq[DocumentSymbol] =
    Seq(DocumentSymbol(name, SymbolKind.Array, false, ranges.range, ranges.selectionRange, children.toList))
}

class HeadersSymbolBuilder(override val element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ParametersSymbolBuilder {
  override protected val name: String = "headers"
}

object HeadersSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.Headers.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new HeadersSymbolBuilder(element))
}

class QueryParametersSymbolBuilder(override val element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ParametersSymbolBuilder {
  override protected val name: String = "queryParameters"
}

object QueryParametersSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.QueryParameters.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new QueryParametersSymbolBuilder(element))
}

class QueryStringSymbolBuilder(override val element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ParametersSymbolBuilder {
  override protected val name: String = "queryString"
}

object QueryStringSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.QueryString.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new QueryStringSymbolBuilder(element))
}

class UriParametersSymbolBuilder(override val element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ParametersSymbolBuilder {
  override protected val name: String = "uriParameters"
}

object UriParametersSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ParametersFieldModel.UriParameters.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new UriParametersSymbolBuilder(element))
}
