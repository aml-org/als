package org.mulesoft.typesystem.typesystem_interfaces

trait IAnnotationInstance {
    def name: String

    def value: Any

    def definition: Option[IParsedType]
}
