package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ParameterStructure extends AMLCompletionPlugin {
  override def id: String = "ParameterStructure"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (request.yPartBranch.isKey && request.fieldEntry.isEmpty) {
        request.amfObject match {
          case p: Parameter if p.binding.option().contains("header") && comesFromHeader(request.yPartBranch) =>
            OasTypeFacetsCompletionPlugin.resolveShape(Option(p.schema).getOrElse(ScalarShape()),
                                                       Nil,
                                                       request.indentation)
          case p: Parameter =>
            Oas20DialectWrapper.paramBiding.toRaw(
              request.indentation,
              CategoryRegistry(ParameterModel.`type`.head.iri(), "in")) +: OasTypeFacetsCompletionPlugin.resolveShape(
              Option(p.schema).getOrElse(ScalarShape()),
              Nil,
              request.indentation)
          case _ => Nil
        }
      } else Nil
    }
  }

  private def comesFromHeader(yPart: YPartBranch) = yPart.keys.contains("headers")
}
