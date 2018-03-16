package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.IUniverse

class ValueType(name: String, _universe: IUniverse = null, path: String = "", description: String = "") extends AbstractType(name,_universe,path) {

    override def hasStructure: Boolean = false

    override def hasValueTypeInHierarchy = true

    override def isValueType = true

    override def isUnion = false

    override def isObject = false
}