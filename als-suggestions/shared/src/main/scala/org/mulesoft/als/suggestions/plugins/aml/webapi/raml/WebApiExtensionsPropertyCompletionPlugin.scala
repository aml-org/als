package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.document.ExtensionLike
import amf.core.remote.Platform
import amf.plugins.domain.webapi.models.WebApi
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, CompletionEnvironment}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLPathCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WebApiExtensionsPropertyCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "WebApiExtensionsPropertyCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.baseUnit match {
      case e: ExtensionLike[WebApi] if request.amfObject.isInstanceOf[WebApi] && request.fieldEntry.isEmpty =>
        suggestOverExtends(e, request.yPartBranch.isKey, request.env, request.prefix)
      case _ => emptySuggestion
    }
  }

  private def suggestOverExtends(e: ExtensionLike[_],
                                 isKey: Boolean,
                                 env: CompletionEnvironment,
                                 prefix: String): Future[Seq[RawSuggestion]] = {
    if (isKey) Future { Seq(RawSuggestion.forKey("extends")) } else
      AMLPathCompletionPlugin.resolveInclusion(e.location().getOrElse(""), env, prefix)
  }
}
