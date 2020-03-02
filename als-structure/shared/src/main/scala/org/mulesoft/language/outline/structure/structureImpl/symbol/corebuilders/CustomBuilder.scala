package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.Field
import amf.core.model.domain.{AmfArray, AmfElement}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.{EncodingModel, OperationModel, WebApiModel}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, SymbolKind}

abstract class CustomBuilder(fe: FieldEntry)(implicit val factory: BuilderFactory) {
  def applies: Boolean
  def build: Seq[DocumentSymbol]
}

abstract class FieldArrayBuilder(fe: FieldEntry)(override implicit val factory: BuilderFactory)
    extends CustomBuilder(fe) {
  protected val ignored: Seq[Field] = Seq(WebApiModel.EndPoints)

  override def applies: Boolean =
    fe.value.value.isInstanceOf[AmfArray] && !ignored.contains(fe.field)

  protected val name: String = fe.field.value.name

  protected final val range: Option[PositionRange] = fe.value.annotations
    .find(classOf[LexicalInformation])
    .map(l => l.range)
    .map(PositionRange(_))

  protected val kind: SymbolKind.Module.type = SymbolKind.Module

  private def getAllDocumentSymbols(fe: FieldEntry, e: AmfElement): Seq[DocumentSymbol] =
    factory
      .builderFor(fe, e.location())
      .map(_.build())
      .getOrElse(Nil)

  protected def children: List[DocumentSymbol] =
    getAllDocumentSymbols(fe, fe.array).toList

  def build: Seq[DocumentSymbol] =
    range
      .map { r =>
        Seq(DocumentSymbol(name, kind, deprecated = false, r, r, children))
      }
      .getOrElse { children }
}

case class WebApiCustomArrayBuilder(fe: FieldEntry)(override implicit val factory: BuilderFactory)
    extends FieldArrayBuilder(fe) {

  protected val ignoreChildren: Seq[Field] = Seq(OperationModel.Tags)

  private val map: Map[Field, String] = Map(
    OperationModel.Tags      -> "tags",
    OperationModel.Responses -> "responses",
    EncodingModel.Headers    -> "headers"
  )

  override val name: String =
    map.getOrElse(fe.field, fe.field.value.name)

  override protected def children: List[DocumentSymbol] = {
    val potentialChildren =
      if (ignoreChildren.contains(fe.field)) Nil
      else super.children
    if (potentialChildren.length == 1 && potentialChildren.head.name == name) // skip lone child with same name
      potentialChildren.head.children
    else
      potentialChildren
  }
}
