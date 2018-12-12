package org.mulesoft.typesystem.typesystem_interfaces

trait IAnnotated {
    def annotations: Seq[IAnnotation]

    def annotation(name: String): Any
}
