package org.mulesoft.typesystem.dialects.extras

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.typesystem.typesystem_interfaces.Extra

class Declaration extends Extra[Integer]{
    override def name: String = "Declaration"

    override def clazz: Class[Integer] = classOf[Integer]

    override def default:Option[Integer] = Some(1)
}

object Declaration extends Declaration {}




