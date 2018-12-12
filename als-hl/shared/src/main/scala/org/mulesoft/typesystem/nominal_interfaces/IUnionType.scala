package org.mulesoft.typesystem.nominal_interfaces

trait IUnionType extends ITypeDefinition {
    def options: Seq[ITypeDefinition]
}
