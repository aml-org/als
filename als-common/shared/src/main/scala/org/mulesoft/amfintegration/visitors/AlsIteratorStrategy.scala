package org.mulesoft.amfintegration.visitors

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.traversal.iterator.AmfIterator
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

object AlsIteratorStrategy {
  def iterator(bu: BaseUnit, amfConfiguration: AmfConfigurationWrapper): AmfIterator =
    new AlsElementIterator(bu, amfConfiguration)
}
