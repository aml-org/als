package org.mulesoft.als.suggestions.test.oas20

import org.mulesoft.typesystem.definition.system.OasResponseCodes

object TestOasResponseCodes {
  val all: Set[String] = OasResponseCodes.all.map(v => v + ":\n          ")
}
