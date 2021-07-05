package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLUnionDiscriminatorCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLUnionDiscriminatorCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      new AMLUnionDiscriminatorCompletionPlugin(params).resolve()
    }
}

class AMLUnionDiscriminatorCompletionPlugin(params: AmlCompletionRequest) extends UnionSuggestions {
  override protected val amfObject: AmfObject     = params.amfObject
  override protected val dialect: Dialect         = params.actualDialect
  override protected val yPartBranch: YPartBranch = params.yPartBranch

  def resolve(): Seq[RawSuggestion] = {
    getUnionType.flatMap(unionMapping => {
      unionMapping
        .typeDiscriminatorName()
        .option()
        .map(name => {
          if (params.yPartBranch.isValueDescendanceOf(name)) {
            unionMapping.typeDiscriminator().keys.map(key => RawSuggestion(key, isAKey = false)).toSeq
          } else Seq.empty
        })
    })
  }.getOrElse(Seq.empty)
}
