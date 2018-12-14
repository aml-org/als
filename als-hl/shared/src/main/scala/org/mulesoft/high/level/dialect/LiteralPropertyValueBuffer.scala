package org.mulesoft.high.level.dialect


import amf.plugins.document.vocabularies.model.domain.DialectDomainElement
import org.mulesoft.high.level.implementation.IValueBuffer
import org.yaml.model.YPart

class LiteralPropertyValueBuffer(
                                element:DialectDomainElement,
                                id: String,
                                value: Any,
                                source: Option[YPart]
                                ) extends IValueBuffer {

    override def getValue: Option[Any] = Some(value)

    override def setValue(value: Any): Unit = {}

    override def yamlNodes: Seq[YPart] = source.map(Seq(_)).getOrElse(Seq())
}
