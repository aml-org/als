package org.mulesoft.typesystem.nominal_interfaces

trait IAnnotationType extends ITypeDefinition {
    def parameters: Seq[ITypeDefinition]

    def allowedTargets: Any
}
