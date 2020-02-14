package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.document.{BaseUnit, Module}
import amf.core.model.domain.AmfObject
import amf.plugins.document.webapi.model.{Overlay, ResourceTypeFragment, TraitFragment, Extension}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UnitDocumentationFacet extends AMLCompletionPlugin {
  override def id: String = "UnitDocumentationFacet"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (request.yPartBranch.isAtRoot && request.yPartBranch.isKey && !isInFieldValue(request) && isNotDocument(
            request)) {
        Seq(RawSuggestion.forKey("usage", "docs", mandatory = false))
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

  private def moduleOrFragment(obj: AmfObject): Boolean =
    obj.isInstanceOf[Extension] || obj.isInstanceOf[Module] || obj.isInstanceOf[Overlay]
}
