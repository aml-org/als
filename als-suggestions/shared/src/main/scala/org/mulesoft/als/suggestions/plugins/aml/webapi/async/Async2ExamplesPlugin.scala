package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.Message
import amf.shapes.client.scala.model.domain.{AnyShape, Example}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExampleSuggestionPluginBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2ExamplesPlugin extends AMLCompletionPlugin with ExampleSuggestionPluginBuilder {
  override def id: String = "ExamplesPlugin"

  def extractShape(request: AmlCompletionRequest): Option[AnyShape] = {
    request.branchStack
      .collectFirst({ case m: Message =>
        if (request.astPartBranch.isInBranchOf("payload")) {
          m.payloads.headOption
            .map(_.schema)
            .collect({ case s: AnyShape => s })
        } else if (request.astPartBranch.isInBranchOf("headers")) {
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
