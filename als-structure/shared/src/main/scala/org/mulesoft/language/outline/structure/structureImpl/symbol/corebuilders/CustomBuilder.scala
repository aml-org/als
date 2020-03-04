package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.Field
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, SymbolKind}
import amf.plugins.domain.webapi.metamodel.{EncodingModel, OperationModel, RequestModel, WebApiModel}

abstract class CustomBuilder(implicit val factory: BuilderFactory) {
  def applies(fe: FieldEntry): Boolean
  def build(fe: FieldEntry): Seq[DocumentSymbol]
}

abstract class FieldArrayBuilder(override implicit val factory: BuilderFactory) extends CustomBuilder {
  protected val ignored: Seq[Field] = Seq(
    WebApiModel.EndPoints,
    RequestModel.UriParameters,
    RequestModel.QueryParameters,
    RequestModel.QueryString,
    RequestModel.UriParameters,
    RequestModel.Headers
  )

  override def applies(fe: FieldEntry): Boolean =
    fe.value.value.isInstanceOf[AmfArray] && !ignored.contains(fe.field)

  protected def name(fe: FieldEntry): String = fe.field.value.name

  protected def range(fe: FieldEntry): Option[PositionRange] =
    fe.value.annotations
      .find(classOf[LexicalInformation])
      .map(l => l.range)
      .map(PositionRange(_))

  protected val kind: SymbolKind.Module.type = SymbolKind.Module

  protected val ignoreChildren: Seq[Field] = Seq()

  private def getAllDocumentSymbols(fe: FieldEntry): Seq[DocumentSymbol] =
    factory
      .builderFor(fe, fe.array.location())
      .map(_.build())
      .getOrElse(Nil)

  def build(fe: FieldEntry): Seq[DocumentSymbol] =
    range(fe)
      .map { r =>
        Seq(DocumentSymbol(name(fe), kind, deprecated = false, r, r, children(fe)))
      }
      .getOrElse { children(fe) }

  protected def children(fe: FieldEntry): List[DocumentSymbol] =
    skipLoneChild(contemplateIgnoredChildren(fe), name(fe))

  private def contemplateIgnoredChildren(fe: FieldEntry): List[DocumentSymbol] =
    if (ignoreChildren.contains(fe.field)) Nil
    else getAllDocumentSymbols(fe).toList

  private def skipLoneChild(children: List[DocumentSymbol], name: String): List[DocumentSymbol] =
    if (children.length == 1 && children.head.name == name)
      children.head.children
    else
      children
}