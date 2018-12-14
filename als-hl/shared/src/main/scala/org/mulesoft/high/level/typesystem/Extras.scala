package org.mulesoft.high.level.typesystem

import amf.core.model.domain.Shape
import amf.core.model.domain.extensions.CustomDomainProperty
import org.mulesoft.typesystem.typesystem_interfaces.Extra

object Extras {

    val SOURCE_SHAPE:Extra[Shape] = new Extra[Shape] {
        override def name: String = "SourceShape"
        override def clazz: Class[Shape] = classOf[Shape]
        override def default:Option[Shape] = None
    }

    val SOURCE_CUSTOM_DOMAIN_PROPERTY:Extra[CustomDomainProperty] = new Extra[CustomDomainProperty] {
        override def name: String = "SourceCustomDomainProperty"
        override def clazz: Class[CustomDomainProperty] = classOf[CustomDomainProperty]
        override def default:Option[CustomDomainProperty] = None
    }

}
