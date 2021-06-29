package org.mulesoft.amfintegration.visitors

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.traversal.iterator.AmfIterator

object AlsIteratorStrategy {
  def iterator(bu: BaseUnit): AmfIterator =
    new AlsElementIterator(bu)
}
