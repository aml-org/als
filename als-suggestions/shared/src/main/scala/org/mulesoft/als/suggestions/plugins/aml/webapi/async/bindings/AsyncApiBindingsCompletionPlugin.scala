package org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings

import amf.plugins.domain.webapi.metamodel.bindings.BindingType
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLEnumCompletionPlugin
import org.mulesoft.als.suggestions.{ObjectRange, RawSuggestion}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.DynamicBindingObjectNode
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsyncApiBindingsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AsyncApiBindingsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (request.fieldEntry.exists(_.field == BindingType.Type)) {
      Future {
        DynamicBindingObjectNode.properties
          .find(_.name().value() == "type")
          .map(AMLEnumCompletionPlugin.suggestMapping)
          .getOrElse(Nil)
          .map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ObjectRange)))
      }
    } else emptySuggestion
  }
}
