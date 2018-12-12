package org.mulesoft.typesystem.typesystem_interfaces

trait IAnnotation extends ITypeFacet {
    def ownerFacet: ITypeFacet

    def owner: Option[IParsedType]

    def name: String

    def value: Any

    def definition: Option[IParsedType]
}
