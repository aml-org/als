package org.mulesoft.typesystem.dialects.extras

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.typesystem.typesystem_interfaces.Extra

class SourceNodeMapping extends Extra[NodeMapping]{
    override def name: String = "SourceNodeMapping"

    override def clazz: Class[NodeMapping] = classOf[NodeMapping]

    override def default:Option[NodeMapping] = None
}

object SourceNodeMapping extends SourceNodeMapping {}


