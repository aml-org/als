package org.mulesoft.high.level.dialect

import amf.plugins.document.vocabularies.model.domain.DialectDomainElement
import org.mulesoft.high.level.implementation.IValueBuffer
import org.yaml.model.YPart

import scala.collection.IndexedSeq

class LiteralArrayPropertyValueBuffer(
                                element:DialectDomainElement,
                                id: String,
                                value: IndexedSeq[Any],
                                source: IndexedSeq[YPart],
                                index: Int
                                ) extends IValueBuffer {

    override def getValue: Option[Any] = {
        if(value.lengthCompare(index) > 0) {
            Some(value(index))
        }
        else {
            None
        }
    }

    override def setValue(value: Any): Unit = {}

    override def yamlNodes: Seq[YPart] = {
        if(source.lengthCompare(index) > 0) {
            Seq(source(index))
        }
        else {
            Seq()
        }
    }
}
