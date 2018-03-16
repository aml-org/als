package org.mulesoft.typesystem.typesystem_interfaces

trait IParsedTypeCollection {
    def getType(name: String): IParsedType

    def add(t: IParsedType): Unit

    def addAnnotationType(t: IParsedType): Unit

    def getAnnotationType(name: String): IParsedType

    def types: Seq[IParsedType]

    def annotationTypes: Seq[IParsedType]

    def getTypeRegistry: ITypeRegistry

    def getAnnotationTypeRegistry: ITypeRegistry

    def library(name: String): IParsedTypeCollection
}
