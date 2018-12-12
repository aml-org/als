package org.mulesoft.typesystem.typesystem_interfaces

trait ITypeRegistry {
    def get(name: String): Option[IParsedType]

    def types: Seq[IParsedType]

    def getByChain(name: String): Option[IParsedType]
}
