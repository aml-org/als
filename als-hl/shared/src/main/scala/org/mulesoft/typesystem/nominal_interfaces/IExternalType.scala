package org.mulesoft.typesystem.nominal_interfaces

trait IExternalType extends ITypeDefinition {
    def schema: Option[String]
}
