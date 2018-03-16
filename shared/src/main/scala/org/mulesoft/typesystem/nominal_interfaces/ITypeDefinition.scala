package org.mulesoft.typesystem.nominal_interfaces

import org.mulesoft.typesystem.typesystem_interfaces.IHasExtra

trait ITypeDefinition extends INamedEntity with IHasExtra {
    def key: Option[NamedId]

    def superTypes: Seq[ITypeDefinition]

    def subTypes: Seq[ITypeDefinition]

    def allSubTypes: Seq[ITypeDefinition]

    def allSuperTypes: Seq[ITypeDefinition]

    def properties: Seq[IProperty]

    def facet(n: String): Option[IProperty]

    def allProperties(visited: scala.collection.Map[String,ITypeDefinition]): Seq[IProperty]

    def allProperties: Seq[IProperty]

    def allFacets: Seq[IProperty]

    def allFacets(visited: scala.collection.Map[String,ITypeDefinition]): Seq[IProperty]

    def facets: Seq[IProperty]

    def isValueType: Boolean

    def hasValueTypeInHierarchy: Boolean

    def isArray: Boolean

    def isObject: Boolean

    def hasArrayInHierarchy: Boolean

    def array: Option[IArrayType]

    def arrayInHierarchy: Option[IArrayType]

    def isUnion: Boolean

    def hasUnionInHierarchy: Boolean

    def union: Option[IUnionType]

    def unionInHierarchy: Option[IUnionType]

    def isAnnotationType: Boolean

    def annotationType: Option[IAnnotationType]

    def hasStructure: Boolean

    def isExternal: Boolean

    def hasExternalInHierarchy: Boolean

    def external: Option[IExternalType]

    def externalInHierarchy: Option[IExternalType]

    def isBuiltIn: Boolean

    //def valueRequirements: Seq[ValueRequirement]

    def universe: IUniverse

    def isAssignableFrom(typeName: String): Boolean

    def property(name: String): Option[IProperty]

    def requiredProperties: Seq[IProperty]

    def getFixedFacets: scala.collection.Map[String,Any]

    def fixedFacets: scala.collection.Map[String,Any]

    def allFixedFacets: scala.collection.Map[String,Any]

    def fixedBuiltInFacets: scala.collection.Map[String,Any]

    def allFixedBuiltInFacets: scala.collection.Map[String,Any]

    def printDetails(indent: String, settings: IPrintDetailsSettings): String

    def printDetails(indent: String): String

    def printDetails: String

    //def examples(collectFromSupertype: Boolean): Seq[IExpandableExample]

    def isGenuineUserDefinedType: Boolean

    def hasGenuineUserDefinedTypeInHierarchy: Boolean

    def genuineUserDefinedTypeInHierarchy: Option[ITypeDefinition]

    def kind: Seq[String]

    //def validate(x: Any): Seq[Status]

    def isTopLevel: Boolean

    def isUserDefined: Boolean
}
