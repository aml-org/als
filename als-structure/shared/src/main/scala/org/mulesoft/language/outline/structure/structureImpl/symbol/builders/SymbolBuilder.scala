package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

/**
  * Common Symbol builder
  * */
trait SymbolBuilder[T] {
  val element: T
  implicit val ctx: StructureContext
  def build(): Seq[DocumentSymbol]
}

trait SymbolBuilderCompanion[T] {
  def getType: Class[_]
  final def constructAny(element: Any)(implicit ctx: StructureContext): Option[SymbolBuilder[T]] = {
    if (getType.isInstance(element)) construct(element.asInstanceOf[T])
    else None
  }

  protected def construct(element: T)(implicit ctx: StructureContext): Option[SymbolBuilder[T]]
}

trait IriSymbolBuilderCompanion {
  val supportedIri: String
}