package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.aml.internal.parse.common.{DeclarationKey, DeclarationKeys}
import amf.core.client.scala.model.domain.{AmfArray, AmfObject, Shape}
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.common.collections._

class DeclaresFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends ArrayFieldTypeSymbolBuilder {

  private lazy val terms: Map[String, String] = ctx.dialect.declarationsMapTerms

  private val groupedDeclarations: Map[String, Seq[AmfObject]] = value.values
    .collect({
      case obj: AmfObject if obj.annotations.trueLocation().contains(ctx.location) => obj
    })
    .legacyGroupBy(declarationName)

  private def getMeta(obj: AmfObject): String =
    obj match {
      case _: Shape => ShapeModel.`type`.head.iri()
      case _        => obj.metaURIs.head
    }

  protected def builderFor(obj: AmfObject): Option[SymbolBuilder[_]] =
    ctx.factory.builderFor(obj)

  private def buildSymbol(name: String, elements: Seq[AmfObject]): Option[DocumentSymbol] = {
    val children: List[DocumentSymbol] = elements
      .flatMap(o =>
        builderFor(o)
          .map(_.build())
          .getOrElse(Nil)
      )
      .sortWith((ds1, ds2) => ds1.range.start < ds2.range.start)
      .toList
    val range: Option[PositionRange] = getDeclarationKeyFor(name).map(key => PositionRange(key.entry.range))
    children match {
      case Nil => None
      case head :: tail =>
        Some(
          DocumentSymbol(name, head.kind, range.getOrElse(head.range + tail.lastOption.getOrElse(head).range), children)
        )
    }
  }

  private lazy val declarationKeys = element.value.annotations.find(classOf[DeclarationKeys])

  private def getDeclarationKeyFor(term: String): Option[DeclarationKey] =
    declarationKeys.flatMap(_.keys.find(_.entry.key.toString() == term))

  protected def declarationName(obj: AmfObject): String =
    terms.getOrElse(getMeta(obj), "unknown")

  override def build(): Seq[DocumentSymbol] =
    groupedDeclarations.flatMap { case (name, elements) =>
      buildSymbol(name, elements)
    }.toSeq
  override protected val optionName: Option[String] = None
}

object DeclaresFieldSymbolBuilderCompanion
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new DeclaresFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
