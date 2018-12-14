package org.mulesoft.typesystem.nominal_interfaces.extras

import org.mulesoft.typesystem.typesystem_interfaces.Extra

class TopLevelExtra extends Extra[Integer]{
    override def name: String = "DefinedAsTopLevelType"

    override def clazz: Class[Integer] = classOf[Integer]

    override def default = Some(1)
}

object TopLevelExtra extends TopLevelExtra {}


