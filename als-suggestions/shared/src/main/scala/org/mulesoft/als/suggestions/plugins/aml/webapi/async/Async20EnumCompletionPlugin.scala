package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLEnumCompletionPlugin

import scala.concurrent.Future

case object Async20EnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.fieldEntry match {
      case Some(FieldEntry(ScalarShapeModel.Format, _)) if params.yPartBranch.isValue => emptySuggestion
      case _                                                                          => AMLEnumCompletionPlugin.resolve(params)
    }
  }
}
