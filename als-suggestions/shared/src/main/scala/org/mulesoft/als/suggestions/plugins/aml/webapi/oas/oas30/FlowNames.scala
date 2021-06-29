package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.apicontract.internal.metamodel.domain.security.OAuth2FlowModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oauth2FlowObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FlowNames extends AMLCompletionPlugin {
  override def id: String = "FlowNames"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    emptySuggestion
    if (request.fieldEntry.exists(_.field == OAuth2FlowModel.Flow)) Future { flowsSuggestion } else emptySuggestion
  }

  private lazy val flowsSuggestion: Seq[RawSuggestion] =
    Oauth2FlowObject.flowProperty.enum().flatMap(_.option().map(_.toString)).map(keySuggestion)

  private def keySuggestion(k: String) = RawSuggestion.forKey(k, "security", mandatory = true)

}
