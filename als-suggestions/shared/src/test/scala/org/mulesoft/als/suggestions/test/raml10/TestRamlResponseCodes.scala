package org.mulesoft.als.suggestions.test.raml10

import org.mulesoft.typesystem.definition.system.RamlResponseCodes

object TestRamlResponseCodes {
  val all: Set[String] = RamlResponseCodes.all.map(v => v + ":\n        ")
}
