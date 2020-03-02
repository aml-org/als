package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.PropertyMapping

trait BindingObjectNode extends DialectNode {

  override def properties: Seq[PropertyMapping] = Seq(
    )
}
