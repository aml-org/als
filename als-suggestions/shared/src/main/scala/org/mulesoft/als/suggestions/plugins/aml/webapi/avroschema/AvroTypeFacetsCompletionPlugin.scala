package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.avroschema.AvroCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{CompletionPluginsRegistryAML, RawSuggestion}
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

import scala.concurrent.Future

object AvroTypeFacetsCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AvroTypeFacetsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val newRequest: AmlCompletionRequest = request.withDefinition(DocumentDefinition(AvroDialect.dialect))

    val pluginsRegistry = new CompletionPluginsRegistryAML
    AvroCompletionPluginRegistry.plugins.foreach(pluginsRegistry.registerPlugin)
    pluginsRegistry.suggests(newRequest)
  }
}
