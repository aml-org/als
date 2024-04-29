package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MapLabelInPropertyMappingCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "MapLabelInPropertyMappingCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    request.amfObject match {
      case pm: PropertyMapping if isMapKeyOrValue(request.astPartBranch) => resolveLabels(pm, request.baseUnit)
      case _                                                             => emptySuggestion
    }

  private def resolveLabels(pm: PropertyMapping, bu: BaseUnit) = Future {
    pm.objectRange().head.option().map(getLabels(_, bu)).getOrElse(Nil).map(RawSuggestion(_, isAKey = false))
  }

  private def isMapKeyOrValue(astPart: ASTPartBranch) =
    astPart.parentEntryIs("mapKey") || astPart.parentEntryIs("mapValue")

  private def getLabels(uri: String, bu: BaseUnit) =
    bu match {
      case d: DeclaresModel =>
        d.declares
          .find(_.id == uri)
          .collect({ case nm: NodeMapping => nm })
          .map(nm => nm.propertiesMapping().flatMap(_.name().option()))
          .getOrElse(Nil)
      case _ => Nil
    }
}
