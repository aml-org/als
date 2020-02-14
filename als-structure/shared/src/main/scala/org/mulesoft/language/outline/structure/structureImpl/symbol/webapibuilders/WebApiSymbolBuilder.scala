package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.Field
import amf.core.metamodel.document.BaseUnitModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain.templates.ParametrizedDeclaration
import amf.core.model.domain.{AmfArray, AmfElement, AmfScalar}
import amf.plugins.domain.shapes.models.CreativeWork
import amf.plugins.domain.webapi.metamodel.WebApiModel
import amf.plugins.domain.webapi.models.{EndPoint, WebApi}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.{
  FatherSymbolBuilder,
  NamedElementSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.RamlEndPointSymbolBuilder
import org.yaml.model.YNode.MutRef

trait BaseUnitSymbolBuilderCompanion extends ElementSymbolBuilderCompanion {

  override type T = BaseUnit
  override def getType: Class[_ <: AmfElement] = classOf[BaseUnit]
  override val supportedIri: String            = BaseUnitModel.`type`.head.iri()
}

class WebApiArraySymbolBuilder(element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {
  override def build(): Seq[DocumentSymbol] =
    element.values
      .flatMap(v => {
        v match {
          case e if e.annotations.find(classOf[SourceAST]).exists(_.ast.isInstanceOf[MutRef]) => None
          case _                                                                              => factory.builderForElement(v)
        }
      })
      .flatMap(_.build())
}

class WebApiSymbolBuilder(override val element: WebApi)(override implicit val factory: BuilderFactory)
    extends FatherSymbolBuilder[WebApi] {

  override def ignoreFields: List[Field] = super.ignoreFields :+ WebApiModel.Servers // todo temp ignore
  val titleChildren: Seq[DocumentSymbol] = element.fields
    .entryJsonld(WebApiModel.Name)
    .map(e => new WebApiTitleSymbolBuilder(e.value.value.asInstanceOf[AmfScalar]).build())
    .getOrElse(Nil)

  val versionChildren: Seq[DocumentSymbol] =
    element.fields
      .entryJsonld(WebApiModel.Version)
      .flatMap(factory.builderFor(_, element.location()))
      .map(_.build())
      .getOrElse(Nil)

  val endpointsChildren: Seq[DocumentSymbol] =
    element.fields
      .entryJsonld(WebApiModel.EndPoints)
      .flatMap(factory.builderFor(_, element.location()))
      .map(_.build())
      .getOrElse(Nil)

  override def build(): Seq[DocumentSymbol] = {
    titleChildren ++ versionChildren ++ super.children
  }
}

object WebApiSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = WebApi
  override val supportedIri: String = WebApiModel.`type`.head.iri()

  override def getType: Class[_ <: AmfElement] = classOf[WebApi]

  override def construct(element: T)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[T]] =
    Some(new WebApiSymbolBuilder(element))
}

trait WebApiScalarBuilder extends ElementSymbolBuilder[AmfScalar] {
  protected val scalar: AmfScalar
  protected val name: String
  private val range = PositionRange(
    scalar.annotations.find(classOf[LexicalInformation]).map(l => l.range).getOrElse(amf.core.parser.Range.NONE))
  override def build(): Seq[DocumentSymbol] =
    Seq(DocumentSymbol(name, SymbolKind.String, deprecated = false, range, range, Nil))
}

class WebApiTitleSymbolBuilder(override val scalar: AmfScalar)(override implicit val factory: BuilderFactory)
    extends NameFieldSymbolBuilder(scalar) {
  override protected val name: String = "title"
}

class NameFieldSymbolBuilder(override val scalar: AmfScalar)(override implicit val factory: BuilderFactory)
    extends WebApiScalarBuilder {
  override protected val name: String = "name"
}

object NameFieldSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfScalar

  override val supportedIri: String = WebApiModel.Name.value.iri()

  override def construct(element: T)(implicit factory: BuilderFactory): Option[NameFieldSymbolBuilder] =
    Some(new NameFieldSymbolBuilder(element))

  override def getType: Class[_ <: AmfElement] = classOf[AmfScalar]
}

class WebApiVersionBuilder(override val scalar: AmfScalar)(override implicit val factory: BuilderFactory)
    extends WebApiScalarBuilder {
  override protected val name: String = "version"
}

object WebApiVersionBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfScalar
  override def getType: Class[_ <: AmfElement] = classOf[AmfScalar]

  override val supportedIri: String = WebApiModel.Version.value.iri()

  override def construct(element: T)(implicit factory: BuilderFactory): Option[WebApiVersionBuilder] =
    Some(new WebApiVersionBuilder(element))
}

object EndPointListBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = WebApiModel.EndPoints.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new EndPointListBuilder(element))
}

class EndPointListBuilder(element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {

  override def build(): Seq[DocumentSymbol] = {

    val endpoints = element.values.collect({ case e: EndPoint => e })
    endpoints
      .collect({
        case e: EndPoint if e.parent.isEmpty => RamlEndPointSymbolBuilder(e, endpoints)(factory)
      })
      .flatMap(_.build())
  }
}

class EndPointSymbolBuilder(override val element: EndPoint)(override implicit val factory: BuilderFactory)
    extends NamedElementSymbolBuilder(element)(factory) {

  override protected val name: String =
    element.path.value().stripPrefix(element.parent.flatMap(_.path.option()).getOrElse(""))
  override protected val selectionRange: Option[PositionRange] =
    element.path.annotations().find(classOf[LexicalInformation]).map(l => PositionRange(l.range)).orElse(range)

  override def children: List[DocumentSymbol] = super.children ++ getExtendsChildren

  private def getExtendsChildren = {
    element.extend.headOption match {
      case Some(first: ParametrizedDeclaration) =>
        val range = first.annotations
          .find(classOf[LexicalInformation])
          .map(l => PositionRange(l.range))
          .getOrElse(PositionRange(amf.core.parser.Range.NONE))
        val end = element.extend.last.annotations
          .find(classOf[LexicalInformation])
          .map(l => PositionRange(l.range))
          .getOrElse(PositionRange(amf.core.parser.Range.NONE))
        val finalRange = range + end
        Some(DocumentSymbol("type", SymbolKind.Interface, deprecated = false, finalRange, range, Nil))
      case _ => None
    }
  }
}

class CreativeWorkListSymbolBuilder(element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {

  private val children = element.values.zipWithIndex
    .map(t => {
      val (e: CreativeWork, index: Int) = t
      val range = PositionRange(
        e.annotations.find(classOf[LexicalInformation]).map(l => l.range).getOrElse(amf.core.parser.Range.NONE))
      DocumentSymbol(index.toString, SymbolKind.Class, deprecated = false, range, range, Nil)
    })
    .toList
  override def build(): Seq[DocumentSymbol] = {
    val newRange = children.head.range + children.last.range
    Seq(DocumentSymbol("documentations", SymbolKind.Array, deprecated = false, newRange, newRange, children))
  }
}

object CreativeWorkListSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = WebApiModel.Documentations.value.iri()

  override def construct(element: T)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new CreativeWorkListSymbolBuilder(element))
}
