package org.mulesoft.als.server.modules.workspace.references.visitors

import amf.core.model.document.BaseUnit
import amf.core.traversal.iterator.AmfIterator

object AlsIteratorStrategy {
  def iterator(bu: BaseUnit): AmfIterator =
    new AlsElementIterator(bu)
}
