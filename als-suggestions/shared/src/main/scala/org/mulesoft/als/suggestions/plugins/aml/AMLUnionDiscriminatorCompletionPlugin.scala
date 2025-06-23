package org.mulesoft.als.suggestions.plugins.aml

import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLUnionDiscriminatorCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLUnionDiscriminatorCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.astPartBranch match {
      case yPartBranch: YPartBranch =>
        Future {
          new AMLUnionDiscriminatorCompletionPlugin(params, yPartBranch).resolve()
        }
      case _ => emptySuggestion
    }

  }
}

class AMLUnionDiscriminatorCompletionPlugin(
    params: AmlCompletionRequest,
    override protected val yPartBranch: YPartBranch
) extends UnionSuggestions {
  override protected val amfObject: AmfObject = params.amfObject
  override protected val documentDefinition: DocumentDefinition     = params.actualDocumentDefinition

  def resolve(): Seq[RawSuggestion] = {
    unionType.flatMap(unionMapping => {
      unionMapping
        .typeDiscriminatorName()
        .option()
        .map(name => {
          if (params.astPartBranch.isValueDescendanceOf(name)) {
            unionMapping.typeDiscriminator().keys.map(key => RawSuggestion(key, isAKey = false)).toSeq
          } else Seq.empty
        })
    })
  }.getOrElse(Seq.empty)
}
