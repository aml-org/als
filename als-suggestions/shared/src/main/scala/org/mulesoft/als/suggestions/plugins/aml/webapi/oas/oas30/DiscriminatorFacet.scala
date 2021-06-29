package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.client.scala.model.domain.Shape
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas3.JsonSchemas

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DiscriminatorFacet extends AMLCompletionPlugin {
  override def id: String = "DiscriminatorFacet"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    request.amfObject match {
      case s: Shape if applies(s, request.fieldEntry) && !DiscriminatorObject.applies(request) =>
        Future { Seq(discriminatorSuggestion) }
      case _ => emptySuggestion
    }

  private lazy val discriminatorSuggestion: RawSuggestion =
    JsonSchemas.discriminatorProperty.toRaw("schemas")

  private def applies(s: Shape, fieldEntry: Option[FieldEntry]) = logicalInheritance(s).nonEmpty && fieldEntry.isEmpty

  private def logicalInheritance(s: Shape): Seq[Shape] = s.or ++ s.xone ++ s.and
}
