package org.mulesoft.typesystem.nominal_interfaces.extras

import org.mulesoft.typesystem.typesystem_interfaces.Extra

class UserDefinedExtra private extends Extra[Integer]{
    override def name: String = "IsUserDefinedType"

    override def clazz: Class[Integer] = classOf[Integer]

    override def default = Some(1)
}

object UserDefinedExtra extends UserDefinedExtra {}
