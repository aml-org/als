package org.mulesoft.typesystem.dialects.extras

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.typesystem.typesystem_interfaces.Extra

class SourcePropertyMapping extends Extra[PropertyMapping]{
    override def name: String = "SourcePropertyMapping"

    override def clazz: Class[PropertyMapping] = classOf[PropertyMapping]

    override def default:Option[PropertyMapping] = None
}

object SourcePropertyMapping extends SourcePropertyMapping {}




