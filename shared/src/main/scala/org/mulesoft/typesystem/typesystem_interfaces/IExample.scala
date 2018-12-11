package org.mulesoft.typesystem.typesystem_interfaces

trait IExample {
    def name: String

    def displayName: String

    def description: String

    def strict: Boolean

    def value: Any

    def annotationsMap: scala.collection.Map[String, Seq[IAnnotation]]

    def annotations: Seq[IAnnotation]
}
