package org.mulesoft.typesystem.nominal_types

import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUniverse}

class EmptyUniverse private extends IUniverse {

    def name:Option[String] = None

    def `type`(name: String): Option[ITypeDefinition] = None

    def version: String = "Empty"

    def types = scala.collection.immutable.List()

    def matched = scala.collection.immutable.Map()

    def parent: Option[IUniverse] = None
}

object EmptyUniverse extends EmptyUniverse {}
