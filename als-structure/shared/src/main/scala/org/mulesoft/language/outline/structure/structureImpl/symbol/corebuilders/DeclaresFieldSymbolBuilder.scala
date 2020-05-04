package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.SourceLocation
import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.amfmanager.AmfImplicits._
import org.mulesoft.language.outline.structure.structureImpl._

class DeclaresFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {

  private val location: Option[String] = value.annotations.find(classOf[SourceLocation]).map(_.location)

  private lazy val terms: Map[String, String] = factory.dialect.declarationsMapTerms

  private val groupedDeclarations: Map[String, Seq[AmfObject]] = value.values
    .collect({ case obj: AmfObject if location.forall(l => obj.location().contains(l)) => obj })
    .groupBy(declarationName)

  private def buildSymbol(name: String, elements: Seq[AmfObject]): Option[DocumentSymbol] = {
    val children: List[DocumentSymbol] = elements
      .flatMap(e => factory.builderFor(e).map(_.build()).getOrElse(Nil))
      .sortWith((ds1, ds2) => ds1.range.start < ds2.range.start).toList
    children match {
      case Nil => None
      case head :: tail =>
        Some(
          DocumentSymbol(name,
                         head.kind,
                         deprecated = false,
                         head.range + tail.lastOption.getOrElse(head).range,
                         head.selectionRange,
                         children))
    }
  }

  protected def declarationName(obj:AmfObject): String = terms.getOrElse(obj.metaURIs.head, "unknowns")

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
