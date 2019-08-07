package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.dialects.OAS20Dialect
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.yaml.model.{YMapEntry, YType}

import scala.concurrent.Future

object OasInBodyParametersCompletionPlugin extends CompletionPlugin {
  override def id: String = "OasInBodyParametersCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case p: Payload if params.yPartBranch.isKey && isParameter(params.yPartBranch) =>
        OAS20Dialect.DialectNodes.BodyParameterObject.propertiesMapping().flatMap(_.name().option()).map { n =>
          RawSuggestion(n, isAKey = true)
        }
      case _ => Nil
    })
  }

  private def isParameter(yPartBranch: YPartBranch): Boolean = yPartBranch.ancestorOf(classOf[YMapEntry]) match {
    case Some(e) => e.key.asScalar.exists(_.text == "parameters") && e.value.tagType == YType.Seq
    case _       => false

  }
}
