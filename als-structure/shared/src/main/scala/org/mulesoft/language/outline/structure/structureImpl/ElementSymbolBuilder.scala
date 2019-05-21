package org.mulesoft.language.outline.structure.structureImpl

import amf.core.model.domain.AmfElement

trait ElementSymbolBuilder[T <: AmfElement] {

  implicit val factory: BuilderFactory
  def build(): Seq[DocumentSymbol]
}

trait ElementSymbolBuilderCompanion {
  type T <: AmfElement
  def getType: Class[_ <: AmfElement]

  def isInstance(element: AmfElement): Boolean = getType.isInstance(element)

  val supportedIri: String
  def construct(element: T)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[_ <: T]]
}
