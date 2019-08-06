package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.AmfObject
import amf.core.model.domain.templates.VariableValue
import amf.plugins.domain.webapi.models.templates.ParametrizedResourceType
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.{CompletionParams, RawSuggestion}

import scala.concurrent.Future

object RamlResourceTypeVariablesImp extends CompletionPlugin {
  override def id: String = "RamlResourceTypeVariablesImp"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if (params.amfObject.isInstanceOf[VariableValue] && params.yPartBranch.isKey) {
        getVariablesFromParent(params.branchStack.headOption).map(RawSuggestion.forKey)
      } else Nil
    )

  }

  private def getVariablesFromParent(parent: Option[AmfObject]): Seq[String] =
    parent
      .collectFirst({ case p: ParametrizedResourceType => p })
      .map(_.target.variables.flatMap(_.option()))
      .getOrElse(Nil)
}
