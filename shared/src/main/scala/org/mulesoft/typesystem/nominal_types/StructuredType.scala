package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition, IUniverse}

import scala.collection.mutable.ListBuffer

class StructuredType(_name:String, _universe:IUniverse = EmptyUniverse, _path: String="") extends AbstractType(_name,_universe,_path) {

    var _properties: ListBuffer[IProperty] = ListBuffer()

    override def hasStructure:Boolean = true

    def propertyIndex(name: String): Int = properties.indexWhere(_.nameId==name)

    def addProperty(name: String, range: ITypeDefinition): Property = {
        new Property(name).withDomain(this).withRange(range)
    }

    def allPropertyIndex(name: String): Int = allProperties.indexWhere(_.nameId==name)

    override def properties: Seq[IProperty] = ListBuffer[IProperty]() ++= _properties

    def registerProperty(p: IProperty):Unit = {
        if (p.domain.isDefined && p.domain.get != this) {
            throw new Error("messageRegistry.SHOULD_BE_ALREADY_OWNED.message")
        }
        if (_properties.contains(p)) {
            throw new Error("messageRegistry.ALREADY_INCLUDED.message")
        }
        _properties += p
    }
}



