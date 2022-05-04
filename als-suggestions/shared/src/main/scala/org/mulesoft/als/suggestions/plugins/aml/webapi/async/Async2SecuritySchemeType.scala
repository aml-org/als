package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.security.SecurityScheme
import amf.apicontract.internal.metamodel.domain.security.SecuritySchemeModel
import org.mulesoft.als.common.ObjectInTree
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.EnumSuggestions
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20SecuritySchemeObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2SecuritySchemeType extends AMLCompletionPlugin with EnumSuggestions {
  override def id: String = "Async2SecuritySchemeType"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.branchStack.headOption match {
      case Some(s: SecurityScheme)
          if ObjectInTree(s, Nil, None, request.astPartBranch).fieldValue
            .exists(_.field == SecuritySchemeModel.Type) =>
        Future(suggestMappingWithEnum(AsyncApi20SecuritySchemeObject.`type`))
      case _ => emptySuggestion
    }
  }
}
