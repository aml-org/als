package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.client.model.domain.OAuth2Flow
import amf.core.model.domain.DataNode
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.webapi.models.security.{OAuth1Settings, OAuth2Settings, ParametrizedSecurityScheme}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10SecuritySchemesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuritySettingsFacetsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySettingsFacetsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      val fromReference =
        request.branchStack.exists(_.isInstanceOf[ParametrizedSecurityScheme]) && request.amfObject
          .isInstanceOf[DataNode]
      request.branchStack.headOption match {
        case Some(_: OAuth1Settings) if !fromReference =>
          Raml10SecuritySchemesDialect.OAuth1Settings.propertiesRaw(d = request.actualDialect)
        case Some(_: OAuth2Settings) if fromReference =>
          Seq(RawSuggestion.arrayProp("scopes", "security"))
        case Some(_: OAuth2Settings) | Some(_: OAuth2Flow)
            if request.fieldEntry.isEmpty && !request.yPartBranch.isInArray || request.amfObject
              .isInstanceOf[DataNode] =>
          oauth2Settings(request.actualDialect)
        case _ => Nil
      }
    }
  }

  private def oauth2Settings(d: Dialect) =
    Raml10SecuritySchemesDialect.OAuth2Flows
      .propertiesRaw(d = d) ++ Raml10SecuritySchemesDialect.OAuth2Settings.propertiesRaw(d = d)
}
