package org.mulesoft.typesystem.nominal_interfaces

trait IAnnotation extends INamedEntity with ITyped {
    def parameterNames: Seq[String]

    def parameter(name: String): Any
}
