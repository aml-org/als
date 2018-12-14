package org.mulesoft.typesystem.nominal_interfaces

trait IExpandableExample {
    def isEmpty: Boolean

    def isJSONString: Boolean

    def isXMLString: Boolean

    def isYAML: Boolean

    def asString: String

    def asJSON: Any

    def original: Any

    def expandAsString: String

    def expandAsJSON: Any

    def isSingle: Boolean

    def strict: Boolean

    def description: String

    def displayName: String

    def annotations: scala.collection.Map[String,IAnnotation]

    def name: String

    def scalarsAnnotations: scala.collection.Map[String,scala.collection.Map[String,IAnnotation]]
}
