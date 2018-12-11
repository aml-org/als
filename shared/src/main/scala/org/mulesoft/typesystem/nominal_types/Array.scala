package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, ITypeDefinition, IUniverse, NamedId}

class Array(_name:String, _universe:IUniverse = EmptyUniverse, _path: String="") extends AbstractType(_name,_universe,_path) with IArrayType {

    var dimensions: Int = 1
    var component: Option[ITypeDefinition] = None

    override def hasArrayInHierarchy = true

    override def isArray = true

    override def isObject = false

    override def arrayInHierarchy: Option[Array] = Some(this)

    override def array:Option[Array] = Some(this)

    override def isUserDefined: Boolean = true

    def componentType:Option[ITypeDefinition] = component

    def setComponent(t: ITypeDefinition):Unit = component = Option(t)

    override def key:Option[NamedId] = None
}