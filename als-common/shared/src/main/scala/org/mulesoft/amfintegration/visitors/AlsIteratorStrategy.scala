package org.mulesoft.amfintegration.visitors

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.traversal.iterator.AmfIterator
import org.mulesoft.amfintegration.amfconfiguration.AmfParseContext

object AlsIteratorStrategy {
  def iterator(bu: BaseUnit, context: AmfParseContext): AmfIterator =
    new AlsElementIterator(bu, context)
}
