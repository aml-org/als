package org.mulesoft.typesystem.typesystem_interfaces

trait IAnnotatedElement {
    def kind: String

    def annotationsMap: scala.collection.Map[String, IAnnotationInstance]

    def annotations: Seq[IAnnotationInstance]

    def value: Any

    def name: String

    def entry: Any
}
