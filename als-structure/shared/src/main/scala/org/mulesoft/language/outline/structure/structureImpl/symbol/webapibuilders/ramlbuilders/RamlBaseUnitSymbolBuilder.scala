package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.core.annotations.SourceLocation
import amf.core.metamodel.Obj
import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.BaseUnitSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.amfmanager.AmfImplicits._
class RamlBaseUnitSymbolBuilder(element: BaseUnit)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(element) {

  override protected def nameFromMeta(obj: Obj): String = obj match {
    case _: ShapeModel             => "types"
    case ResourceTypeModel         => "resourceTypes"
    case TraitModel                => "traits"
    case SecuritySchemeModel       => "securitySchemes"
    case CustomDomainPropertyModel => "annotationTypes"
    case _                         => "unknown"
  }
}

object RamlBaseUnitSymbolBuilder extends BaseUnitSymbolBuilderCompanion {

  override def construct(element: T)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[BaseUnit]] =
    Some(new RamlBaseUnitSymbolBuilder(element))
}

class DeclaresFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {

  private val location: Option[String] = value.annotations.find(classOf[SourceLocation]).map(_.location)
  val groupedDeclarations: Map[String, Seq[AmfObject]] = value.values
    .collect({ case obj: AmfObject if location.forall(l => obj.location().contains(l)) => obj })
    .groupBy(d => d.metaURIs.head)

  private val terms: Map[String, String] = factory.dialect.declarationsMapTerms

  private def buildSymbol(name: String, elements: Seq[AmfObject]) = {
    val children = elements
      .flatMap(e => factory.builderFor(e).map(_.build()).getOrElse(Nil))
      .sortWith((ds1, ds2) => ds1.range.start < ds2.range.start)
    children.toList match {
      case Nil => None
      case head :: tail =>
        Some(
          DocumentSymbol(name,
                         head.kind,
                         deprecated = false,
                         head.range + tail.lastOption.getOrElse(head).range,
                         head.selectionRange,
                         children.toList))
    }
  }

  override def build(): Seq[DocumentSymbol] = {
    groupedDeclarations.flatMap { case (meta, elements) => terms.get(meta).flatMap(buildSymbol(_, elements)) }.toSeq
  }
}

object DeclaresFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
