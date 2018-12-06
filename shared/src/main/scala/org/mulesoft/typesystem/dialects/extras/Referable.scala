package org.mulesoft.typesystem.dialects.extras

import org.mulesoft.typesystem.typesystem_interfaces.Extra

class Referable extends Extra[Integer] {
    override def name: String = "Referable"

    override def clazz: Class[Integer] = classOf[Integer]

    override def default: Option[Integer] = Some(1)
}

object Referable extends Referable {}
