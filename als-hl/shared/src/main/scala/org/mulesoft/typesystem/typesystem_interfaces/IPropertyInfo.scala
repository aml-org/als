package org.mulesoft.typesystem.typesystem_interfaces

trait IPropertyInfo {
    def name: String

    def required: Boolean

    def range: IParsedType

    def declaredAt: IParsedType

    def isPattern: Boolean

    def isAdditional: Boolean

    def annotations: Seq[IAnnotation]
}
