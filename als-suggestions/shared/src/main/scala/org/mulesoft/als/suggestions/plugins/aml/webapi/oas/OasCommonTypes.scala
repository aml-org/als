package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.Shape
import amf.dialects.OAS20Dialect
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OasCommonTypes extends AMLCompletionPlugin {
  override def id: String = "OasCommonTypes"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case a: Shape if isType(request.yPartBranch) =>
          OAS20Dialect.shapesPropertyMapping
            .enum()
            .flatMap(_.option().map(_.toString))
            .map(RawSuggestion(_, isAKey = false))
        case _ => Nil
      }
    }
  }

  private def isType(yPart: YPartBranch) = yPart.parentEntryIs("type") && yPart.isValue
}
