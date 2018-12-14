package org.mulesoft.typesystem.nominal_interfaces

trait INamedEntity {
    def nameId: Option[String]

    def description: String
//
//    def getAdapter[T]: T

    def annotations: Seq[IAnnotation]

    def addAnnotation(a: IAnnotation): Unit

    def removeAnnotation(a: IAnnotation): Unit
//
//    def getAdapters: Seq[Any]
}
