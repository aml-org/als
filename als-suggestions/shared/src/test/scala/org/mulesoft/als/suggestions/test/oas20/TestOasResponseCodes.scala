package org.mulesoft.als.suggestions.test.oas20

import org.mulesoft.typesystem.definition.system.OasResponseCodes

object TestOasResponseCodes {
  val all: Seq[String] = OasResponseCodes.all.map(v => v + ":\n          ")
}
