package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition
import org.mulesoft.typesystem.nominal_interfaces.extras.BuiltInExtra

object BuiltinUniverse {

    private val _instance: Universe = new Universe("builtin", None, "")

    def instance: Universe = _instance


    val ANY: ITypeDefinition = addStructuredType("any")

    val OBJECT: ITypeDefinition = addStructuredType("object", ANY)

    val SCALAR: ITypeDefinition = addValueType("scalar", ANY)

    val ARRAY: ITypeDefinition = addStructuredType("array", ANY)

    val EXTERNAL: ITypeDefinition = addStructuredType("external", ANY)

    val NUMBER: ITypeDefinition = addValueType("number", ANY)

    val INTEGER: ITypeDefinition = addValueType("integer", NUMBER)

    val FLOAT: ITypeDefinition = addValueType("float", NUMBER)

    val BOOLEAN: ITypeDefinition = addValueType("boolean", SCALAR)

    val STRING: ITypeDefinition = addValueType("string", SCALAR)

    val NIL: ITypeDefinition = addValueType("nil", SCALAR)

    val DATE_ONLY: ITypeDefinition = addValueType("date-only", SCALAR)

    val TIME_ONLY: ITypeDefinition = addValueType("time-only", SCALAR)

    val DATETIME_ONLY: ITypeDefinition = addValueType("datetime-only", SCALAR)

    val DATETIME: ITypeDefinition = addValueType("datetime", SCALAR)

    val FILE: ITypeDefinition = addValueType("file", SCALAR)

    val UNION: ITypeDefinition = addStructuredType("union", ANY)


    private def addStructuredType(name: String): StructuredType = {
        val result = new StructuredType(name, instance)
        result.putExtra(BuiltInExtra)
        instance.register(result)
        result
    }

    private def addStructuredType(name: String, superType: ITypeDefinition): StructuredType = {
        val result = new StructuredType(name, instance)
        result.putExtra(BuiltInExtra)
        result.addSuperType(superType)
        instance.register(result)
        result
    }

    private def addValueType(name: String, superType: ITypeDefinition): ValueType = {
        val result = new ValueType(name, instance)
        result.putExtra(BuiltInExtra)
        result.addSuperType(superType)
        instance.register(result)
        result
    }
}
