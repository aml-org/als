package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.structure

import amf.apicontract.client.scala.model.domain.Payload
import amf.core.client.scala.model.domain.{AmfObject, Shape}
import amf.shapes.client.scala.model.domain.Example
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin

import scala.concurrent.Future

trait SchemaExampleException {
  def applies(request: AmlCompletionRequest): Boolean = {
    anonymousExample(request.amfObject) && request.fieldEntry.isEmpty && request.branchStack.headOption.exists(h =>
      h.isInstanceOf[Shape] || h.isInstanceOf[Payload]
    )
  }

  private def anonymousExample(amfObject: AmfObject) = {
    amfObject match {
      case e: Example => e.name.option().isEmpty
      case _          => false
    }
  }
}

object SchemaExampleException extends ResolveIfApplies with SchemaExampleException {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    if (applies(request)) Some(Future.successful(Nil)) else notApply
}

object SchemaExampleStructure extends ExceptionPlugin with SchemaExampleException {

  override def id: String = "SchemaExampleStructure"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = emptySuggestion
}
