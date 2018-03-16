package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{IArrayType, IExternalType, IUniverse}

class ExternalType(_name:String, _universe:IUniverse = EmptyUniverse, _path: String="") extends AbstractType(_name,_universe,_path) with IExternalType {

    var schemaString: Option[String] = None

    override def externalInHierarchy = Some(this)

    override def typeId: Option[String] = schemaString

    def schema: Option[String] = schemaString

    override def isUserDefined: Boolean = true

    override def hasExternalInHierarchy = true

    override def isExternal = true

    override def external = Some(this)
}