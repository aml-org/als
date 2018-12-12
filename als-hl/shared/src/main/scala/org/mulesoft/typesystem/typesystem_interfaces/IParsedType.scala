package org.mulesoft.typesystem.typesystem_interfaces

trait IParsedType extends IAnnotated with IHasExtra {
    def subTypes: Seq[IParsedType]

    def superTypes: Seq[IParsedType]

    def name: String

    def examples: Seq[IExample]

    def allOptions: Seq[IParsedType]

    def allSubTypes: Seq[IParsedType]

    def allSuperTypes: Seq[IParsedType]

    def annotations: Seq[IAnnotation]

    def annotation(name: String): Any

    def declaredAnnotations: Seq[IAnnotation]

    def scalarsAnnotations: scala.collection.Map[String, Seq[Seq[IAnnotation]]]

    def declaredScalarsAnnotations: scala.collection.Map[String, Seq[Seq[IAnnotation]]]

    def registry: Option[IParsedTypeCollection]

    def isAssignableFrom(t: IParsedType): Boolean

    def componentType: Option[IParsedType]

    def properties: Seq[IPropertyInfo]

    def declaredProperties: Seq[IPropertyInfo]

    def definedFacets: Seq[IPropertyInfo]

    def allDefinedFacets: Seq[IPropertyInfo]

    def property(name: String): IPropertyInfo

    //def validate(i: Any, autoClose: Boolean): IStatus

    //def validateType(reg: ITypeRegistry): IStatus

    //def ac(i: Any): IParsedType

    //def canDoAc(i: Any): IStatus

    def allFacets: Seq[ITypeFacet]

    def exampleObject: Any

    def declaredFacets: Seq[ITypeFacet]

    def customFacets: Seq[ITypeFacet]

    def allCustomFacets: Seq[ITypeFacet]

    def restrictions: Seq[IConstraint]

    def isAnonymous: Boolean

    def isEmpty: Boolean

    def isObject: Boolean

    def isExternal: Boolean

    def isString: Boolean

    def isNumber: Boolean

    def isBuiltin: Boolean

    def isBoolean: Boolean

    def isInteger: Boolean

    def isDateTime: Boolean

    def isDateOnly: Boolean

    def isTimeOnly: Boolean

    def isDateTimeOnly: Boolean

    def isArray: Boolean

    def isScalar: Boolean

    def isUnion: Boolean

    def isIntersection: Boolean

    def isUnknown: Boolean

    def isFile: Boolean

    def isRecurrent: Boolean

    def options: Seq[IParsedType]

    def cloneWithFilter(
                           x: (ITypeFacet, IParsedType) => Option[ITypeFacet],
                           f: Option[IParsedType => IParsedType]): IParsedType

    def kind: String
}
