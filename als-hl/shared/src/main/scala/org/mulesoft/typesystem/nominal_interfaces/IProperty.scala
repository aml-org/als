package org.mulesoft.typesystem.nominal_interfaces

import org.mulesoft.typesystem.typesystem_interfaces.IHasExtra

trait IProperty extends INamedEntity with IHasExtra {

    def matchKey(k: String): Boolean

    def range: Option[ITypeDefinition]

    def domain: Option[ITypeDefinition]

    def isRequired: Boolean

    def isMultiValue: Boolean

    def isPrimitive: Boolean

    def isValueProperty: Boolean

    def keyPrefix: Option[String]

    def getKeyRegexp: Option[String]

    def defaultValue: Option[Any]

    def enumOptions: Option[Seq[String]]

    def isDescriminator: Boolean
}
