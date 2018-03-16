package org.mulesoft.typesystem.nominal_interfaces

trait IUniverse {

    def name: Option[String]

    def `type`(name: String): Option[ITypeDefinition]

    def version: String

    def types: Seq[ITypeDefinition]

    def parent:Option[IUniverse]

    //def matched: scala.collection.Map[String, NamedId]
}
