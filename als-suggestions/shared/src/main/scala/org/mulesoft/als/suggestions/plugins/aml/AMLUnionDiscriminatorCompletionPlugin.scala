package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.domain.AmfObject
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
          if (params.yPartBranch.isValueDescendantOf(name)) {
            unionMapping.typeDiscriminator().keys.map(key => RawSuggestion(key, isAKey = false)).toSeq
          } else Seq.empty
        })
    })
  }.getOrElse(Seq.empty)
}
