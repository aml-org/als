package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUnionType, IUniverse, NamedId}

class Union(_name:String, _universe:IUniverse = EmptyUniverse, _path: String="") extends AbstractType(_name,_universe,_path) with IUnionType {

    override def key: Option[NamedId] = None

    var _options:Seq[ITypeDefinition] = Seq()

    def options: Seq[ITypeDefinition] = _options

    def setOptions(options:Seq[ITypeDefinition]):Unit = _options = options

    override def isUserDefined: Boolean = true

    override def unionInHierarchy:Option[Union] = Some(this)

    override def union:Option[Union] = Some(this)

    override def hasUnionInHierarchy = true

    override def isUnion = true

    override def isObject:Boolean = _options.forall(_.isObject)

    override def hasArrayInHierarchy:Boolean = _options.exists(_.hasArrayInHierarchy)
}