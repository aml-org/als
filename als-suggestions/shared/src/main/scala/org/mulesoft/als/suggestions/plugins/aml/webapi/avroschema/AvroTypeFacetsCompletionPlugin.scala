package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.aml.avroschema.AvroCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AvroTypeFacetsCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AvroTypeFacetsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    // dejar mas lindo esto, capaz poniendo un override del build que reciba una request vieja y el dialecto nuevo solamente
    val newRequest: AmlCompletionRequest = AmlCompletionRequestBuilder.build(
      request.baseUnit,
      request.position.toAmfPosition,
      AvroDialect.dialect,
      request.directoryResolver,
      false,
      request.rootUri,
      request.configurationReader,
      request.completionsPluginHandler,
      request.alsConfigurationState
    )
    Future.sequence {
      AvroCompletionPluginRegistry.plugins.map{ p =>
        val eventualSuggestions = p.resolve(newRequest)
        // este foreach es solo para debuggear, se puede borrar si no hace falta o dejar comentado
        eventualSuggestions.foreach{ s =>
          println(s)
          println(p.id)
        }
        eventualSuggestions
      }
    }.map(_.flatten)
  }
}
