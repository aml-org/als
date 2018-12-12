package org.mulesoft.typesystem.nominal_interfaces.extras

import org.mulesoft.typesystem.typesystem_interfaces.Extra

class BuiltInExtra extends Extra[Integer]{
    override def name: String = "DefinedAsBuiltInType"

    override def clazz: Class[Integer] = classOf[Integer]

    override def default:Option[Integer] = Some(1)
}

object BuiltInExtra extends BuiltInExtra {}
