package org.mulesoft.typesystem.typesystem_interfaces

trait IDerivedType extends IParsedType {
    def options: Seq[IParsedType]

    def allOptions: Seq[IParsedType]
}
