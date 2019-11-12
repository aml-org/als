package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.Type
import amf.core.model.domain.AmfObject
import org.mulesoft.als.suggestions.RawSuggestion

trait BooleanSuggestions {
  val booleanSuggestions: Seq[RawSuggestion] = Seq("true", "false").map(RawSuggestion.forBool(_))
  def isBoolean(h: AmfObject, text: String): Boolean =
    h.fields.fields().find(_.field.value.name == text).exists(_.field.`type` == Type.Bool)
}
