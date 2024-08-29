package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.domain.PropertyMapping
import org.mulesoft.als.common.ASTPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object AMLEnumCompletionPlugin extends AMLCompletionPlugin with EnumSuggestions {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      if (params.astPartBranch.isValue || params.astPartBranch.isInArray && params.fieldEntry.isDefined)
        getSuggestions(params.propertyMapping, params.astPartBranch)
      else Nil
    }

  def getSuggestions(propertyMapping: List[PropertyMapping], astPartBranch: ASTPartBranch): Seq[RawSuggestion] =
    propertyMapping match {
      case head :: Nil => suggestMappingWithEnum(head)
      case Nil         => Nil
      case _ =>
        propertyMapping
          .find(pm => astPartBranch.parentKey.exists(s => pm.name().option().contains(s)))
          .map(suggestMappingWithEnum)
          .getOrElse(Nil)
    }
}
