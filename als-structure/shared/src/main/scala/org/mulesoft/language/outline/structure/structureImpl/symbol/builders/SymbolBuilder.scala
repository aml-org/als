package org.mulesoft.language.outline.structure.structureImpl.symbol.builders
import amf.core.parser.Range
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.SymbolKinds.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

/**
  * Common Symbol builder
  * */
trait SymbolBuilder[T] {

  def element: T
  implicit val ctx: StructureContext
  protected def optionName: Option[String]
  protected def children: List[DocumentSymbol]
  private def sortedChildren = children.sortWith((ds1, ds2) => ds1.range.start < ds2.range.start)

  protected def kind: SymbolKind
  protected def range: Option[Range]

  private def rangeFromChildren: Option[PositionRange] = sortedChildren match {
    case head :: Nil  => Some(head.range)
    case head :: tail => Some(PositionRange(head.range.start, tail.last.range.end))
    case _            => None
  }

  private def effectiveRange: Option[PositionRange] =
    range.map(PositionRange(_)).orElse(rangeFromChildren)

  def build(): Seq[DocumentSymbol] = {
    optionName match {
      case Some(name) =>
        build(name).toSeq
      case _ => children
    }
  }

  def build(name: String): Option[DocumentSymbol] = {
    effectiveRange.map { ef =>
      DocumentSymbol(name, kind, ef, skipLoneChild(children, name))
    }
  }

  protected def skipLoneChild(children: List[DocumentSymbol], name: String): List[DocumentSymbol] =
    if (children.length == 1 && children.head.name == name)
      children.head.children
    else
      children
}

trait SymbolBuilderCompanion[T] {
  def getType: Class[_]
  final def constructAny(element: Any)(implicit ctx: StructureContext): Option[SymbolBuilder[T]] = {
    if (getType.isInstance(element))
      construct(element.asInstanceOf[T])
    else None
  }

  protected def construct(element: T)(implicit ctx: StructureContext): Option[SymbolBuilder[T]]
}

trait IriSymbolBuilderCompanion {
  val supportedIri: String
}
