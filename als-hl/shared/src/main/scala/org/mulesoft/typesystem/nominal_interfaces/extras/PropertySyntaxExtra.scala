// $COVERAGE-OFF$
package org.mulesoft.typesystem.nominal_interfaces.extras

import org.mulesoft.typesystem.typesystem_interfaces.Extra

class PropertySyntaxExtra extends Extra[PropertySyntaxExtra]{

    override def name: String = "SyntaxDetails"

    override def clazz: Class[PropertySyntaxExtra] = classOf[PropertySyntaxExtra]

    override def default: Option[PropertySyntaxExtra] = None

    private var _isKey:Boolean = false

    private var _isValue:Boolean = false

    private var _isEmbeddedInMaps:Boolean = false

    private var _isEmbeddedInArray:Boolean = false

    private var _isExample:Boolean = false

    private var _isHiddenFromUi:Boolean = false

    private var _enum: Seq[Any] = Seq()
    
    private var _oftenKeys: Seq[Any] = Seq()

    private var _parentPropertiesRestriction: Seq[String] = Seq()
    
    private var sufficient = false

    def isKey:Boolean = _isKey

    def isValue:Boolean = _isValue

    def isEmbeddedInMaps:Boolean = _isEmbeddedInMaps

    def isEmbeddedInArray:Boolean = _isEmbeddedInArray

    def isExample:Boolean = _isExample

    def isHiddenFromUI:Boolean = _isHiddenFromUi

    def enum:Seq[Any] = _enum
    
    def oftenValues: Seq[Any] = _oftenKeys

    def parentPropertiesRestriction: Seq[String] = _parentPropertiesRestriction

    def isSufficient:Boolean = sufficient

    def setIsKey():Unit = {
        _isKey = true
        sufficient = true
    }

    def setIsValue():Unit = {
        _isValue = true
        sufficient = true
    }

    def setIsEmbeddedInMaps():Unit = {
        _isEmbeddedInMaps = true
        sufficient = true
    }

    def setIsEmbeddedInArray():Unit = {
        _isEmbeddedInArray = true
        sufficient = true
    }

    def setIsHiddenFromUI():Unit = {
        _isHiddenFromUi = true
        sufficient = true
    }

    def setIsExample():Unit = {
        _isExample = true
        sufficient = true
    }
    
    def setEnum(value:Seq[Any]):Unit = {
        _enum = value
        sufficient = _enum.nonEmpty
    }
    
    def setOftenValues(value:Seq[Any]): Unit = {
		_oftenKeys = value
        sufficient = true
    }

    def setParentPropertiesRestriction(value: Seq[String]): Unit = {
        _parentPropertiesRestriction = value
        sufficient = true
    }

    def allowsParentProperty(pNameOpt:Option[String]):Boolean =
        _parentPropertiesRestriction.isEmpty || ( pNameOpt.isDefined &&
            _parentPropertiesRestriction.contains(pNameOpt.get))
}

private class DefaultProperySyntaxExtra extends PropertySyntaxExtra{

    override def isSufficient:Boolean = false

    override def setIsKey():Unit = {}

    override def setIsValue():Unit = {}

    override def setIsEmbeddedInMaps():Unit = {}

    override def setIsEmbeddedInArray():Unit = {}

    override def setIsExample():Unit = {}

    override def setEnum(value:Seq[Any]):Unit = {}
    
    override def setOftenValues(value:Seq[Any]):Unit = {}

    override def setParentPropertiesRestriction(value:Seq[String]):Unit = {}
}

object PropertySyntaxExtra extends PropertySyntaxExtra{

    val empty:PropertySyntaxExtra = new DefaultProperySyntaxExtra()

    def apply():PropertySyntaxExtra = new PropertySyntaxExtra()
}
// $COVERAGE-ON$