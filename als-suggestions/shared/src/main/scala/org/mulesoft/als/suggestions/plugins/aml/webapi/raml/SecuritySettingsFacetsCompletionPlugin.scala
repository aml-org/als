package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.security.{
  OAuth1Settings,
  OAuth2Flow,
  OAuth2Settings,
  ParametrizedSecurityScheme
}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10SecuritySchemesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuritySettingsFacetsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySettingsFacetsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (request.fieldEntry.nonEmpty) emptySuggestion
    else {
      Future {
        val fromReference =
          request.branchStack.exists(_.isInstanceOf[ParametrizedSecurityScheme])
        request.amfObject match {
          case _: OAuth1Settings if !fromReference =>
            Raml10SecuritySchemesDialect.OAuth1Settings.propertiesRaw(fromDefinition = request.actualDocumentDefinition)
          case _: OAuth2Settings if fromReference =>
            Seq(RawSuggestion.arrayProp("scopes", "security"))
          case _: OAuth2Settings | _: OAuth2Flow if !fromReference =>
            oauth2Settings(request.actualDocumentDefinition)
          case _ => Nil
        }
      }
    }
  }

  private def oauth2Settings(d: DocumentDefinition) =
    Raml10SecuritySchemesDialect.OAuth2Flows
      .propertiesRaw(fromDefinition = d) ++ Raml10SecuritySchemesDialect.OAuth2Settings.propertiesRaw(fromDefinition = d)
}
