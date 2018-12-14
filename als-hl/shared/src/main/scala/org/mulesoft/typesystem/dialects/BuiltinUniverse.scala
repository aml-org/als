package org.mulesoft.typesystem.dialects

import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.{StructuredType, Universe, ValueType}

object BuiltinUniverse {

    def getInstance:IUniverse = instance.get

    def any:ITypeDefinition = _anyType

    def number:ITypeDefinition = _numberType

    def string:ITypeDefinition = _stringType

    def integer:ITypeDefinition = _integerType

    def boolean:ITypeDefinition = _booleanType

    def float:ITypeDefinition = _floatType

    def decimal:ITypeDefinition = _decimalType

    def double:ITypeDefinition = _doubleType

    def duration:ITypeDefinition = _durationType

    def dateTime:ITypeDefinition = _dateTimeType

    def time:ITypeDefinition = _timeType

    def date:ITypeDefinition = _dateType

    def uri:ITypeDefinition = _uriType



    private var instance:Option[Universe] = Some(new Universe("builtins",None,"1.0"))

    private val _anyType = new StructuredType("any", instance.get, "/any")

    private val _numberType = new ValueType("number", instance.get, "/number")

    private val _stringType = new ValueType("string", instance.get, "/string")

    private val _integerType = new ValueType("integer", instance.get, "/integer")

    private val _booleanType = new ValueType("boolean", instance.get, "/boolean")

    private val _floatType = new ValueType("float", instance.get, "/float")

    private val _decimalType = new ValueType("decimal", instance.get, "/decimal")

    private val _doubleType = new ValueType("double", instance.get, "/double")

    private val _durationType = new ValueType("duration", instance.get, "/duration")

    private val _dateTimeType = new ValueType("dateTime", instance.get, "/dateTime")

    private val _timeType = new ValueType("time", instance.get, "/time")

    private val _dateType = new ValueType("date", instance.get, "/date")

    private val _uriType = new ValueType("uri", instance.get, "/uri")

    _numberType.addSuperType(_anyType)
    _stringType.addSuperType(_anyType)
    _booleanType.addSuperType(_anyType)
    _durationType.addSuperType(_anyType)
    _dateTimeType.addSuperType(_anyType)
    _timeType.addSuperType(_anyType)
    _dateType.addSuperType(_anyType)
    _uriType.addSuperType(_anyType)
    _floatType.addSuperType(_numberType)
    _doubleType.addSuperType(_numberType)
    _decimalType.addSuperType(_numberType)
    _integerType.addSuperType(_decimalType)


    instance.get.registerAlias("anyType", _anyType)
    instance.get.registerAlias("anyUri", _uriType)

    instance.get.register(_anyType)
    instance.get.register(_numberType)
    instance.get.register(_stringType)
    instance.get.register(_booleanType)
    instance.get.register(_durationType)
    instance.get.register(_dateTimeType)
    instance.get.register(_timeType)
    instance.get.register(_dateType)
    instance.get.register(_uriType)
    instance.get.register(_floatType)
    instance.get.register(_doubleType)
    instance.get.register(_decimalType)
    instance.get.register(_integerType)

}
