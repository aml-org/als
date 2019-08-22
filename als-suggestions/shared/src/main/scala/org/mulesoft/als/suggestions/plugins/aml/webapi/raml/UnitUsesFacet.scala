package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.document.{BaseUnit, Fragment, Module}
import amf.core.model.domain.AmfObject
import amf.core.model.domain.templates.AbstractDeclaration
import amf.plugins.document.webapi.model.{ResourceTypeFragment, TraitFragment}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UnitUsesFacet extends AMLCompletionPlugin {
  override def id: String = "UnitUsesFacet"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (request.yPartBranch.isAtRoot && request.yPartBranch.isKey && !isInFieldValue(request) && isNotDocument(
            request)) {
        Seq(RawSuggestion.forKey("usage", "docs"))
      } else Nil
    }
  }

  private def isNotDocument(request: AmlCompletionRequest): Boolean = {
    request.branchStack.lastOption match {
      case Some(_: ResourceTypeFragment | _: TraitFragment) => false
      case Some(u: BaseUnit)                                => moduleOrFragment(u)
      case None                                             => moduleOrFragment(request.amfObject)
      case _                                                => false
    }
  }

  private def moduleOrFragment(obj: AmfObject): Boolean = obj.isInstanceOf[Fragment] || obj.isInstanceOf[Module]
}
