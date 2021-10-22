package org.mulesoft.language.outline.structure.structureImpl.symbol.builders

import amf.core.client.scala.model.domain.AmfObject

/**
  * Builder for nodes that have structure(name, range, etc) and not should be skipped to show their sons
  *
  * @tparam T
  */
trait StructuredSymbolBuilder[T <: AmfObject] extends AmfObjectSymbolBuilder[T] {}
