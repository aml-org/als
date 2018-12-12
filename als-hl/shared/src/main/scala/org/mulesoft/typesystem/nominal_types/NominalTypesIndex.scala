package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.IUniverse

object NominalTypesIndex {

//    type IAnnotation = IAnnotation
//    type ITypeDefinition = ITypeDefinition
//    type IExpandableExample = IExpandableExample
//    type IUniverse = IUniverse
//    type IUnionType = IUnionType
//    type IProperty = IProperty
//    type IArrayType = IArrayType
//    type NamedId = NamedId
//    type IExternalType = IExternalType
//    type FacetValidator = FacetValidator
//    type IPrintDetailsSettings = IPrintDetailsSettings
//    type IAnnotationType = IAnnotationType
//    type INamedEntity = INamedEntity
    //var messageRegistry = require("../../resources/errorMessages")

    //def require(s: String): Any

//    val extraInjections: Array[Injector] = Array()
//
//    def registerInjector(i: Injector) = {
//        extraInjections.push(i)
//    }

    def getUniqueTypeId(t: AbstractType): Option[String] = {
        var uniqueTypeId = t.typeId
        if (uniqueTypeId==null) {
//            var adapter = t.getAdapter(InheritedType)
//            (uniqueTypeId = (((adapter && ((adapter.id() + "")))) || ""))
        }
        uniqueTypeId
    }
}
