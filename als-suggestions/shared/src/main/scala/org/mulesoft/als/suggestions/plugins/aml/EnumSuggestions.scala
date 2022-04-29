package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.RawSuggestion

trait EnumSuggestions {
  protected def suggestMappingWithEnum(pm: PropertyMapping): Seq[RawSuggestion] =
    pm.enum()
      .flatMap(_.option().map(e => {
        val raw = pm.toRaw("unknown")
        raw.copy(
          newText = e.toString,
          displayText = e.toString,
          description = e.toString,
          options = raw.options.copy(isKey = false)
        )
      }))

}
