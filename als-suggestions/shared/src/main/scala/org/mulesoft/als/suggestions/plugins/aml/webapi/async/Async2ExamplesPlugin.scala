package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.Shape
import amf.plugins.domain.shapes.models.Example
import amf.plugins.domain.webapi.models.Message
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExampleSuggestionPluginBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2ExamplesPlugin extends AMLCompletionPlugin with ExampleSuggestionPluginBuilder {
  override def id: String = "ExamplesPlugin"

  def extractShape(request: AmlCompletionRequest): Option[Shape] = {
    request.branchStack
      .collectFirst({
        case m: Message =>
          if (request.yPartBranch.isInBranchOf("payload")) {
            m.payloads.headOption
              .map(_.schema)
          } else if (request.yPartBranch.isInBranchOf("headers")) {
            Some(m.headerSchema)
          } else {
            None
          }
      })
      .flatten
  }

  def resolveForMessage(request: AmlCompletionRequest, example: Example): Option[Seq[RawSuggestion]] =
    extractShape(request)
      .flatMap(shape => suggestionsForShape(example, shape, request))
      .map(_.suggest())

  def resolveForPayloadOrHeader(request: AmlCompletionRequest, example: Example): Option[Seq[RawSuggestion]] = {
    buildPluginFromExample(example, request)
      .map(_.suggest())
      .flatMap(Some(_))
  }

  private def getExample(request: AmlCompletionRequest): Option[Example] =
    request.branchStack
      .collectFirst({ case e: Example => e })

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      getExample(request)
        .flatMap(example => {
          resolveForPayloadOrHeader(request, example)
            .orElse(resolveForMessage(request, example))
        })
        .getOrElse(Seq())
    }
  }
}
