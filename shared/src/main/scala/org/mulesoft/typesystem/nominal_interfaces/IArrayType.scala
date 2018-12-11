package org.mulesoft.typesystem.nominal_interfaces

trait IArrayType extends ITypeDefinition {
    def componentType: Option[ITypeDefinition]
}
