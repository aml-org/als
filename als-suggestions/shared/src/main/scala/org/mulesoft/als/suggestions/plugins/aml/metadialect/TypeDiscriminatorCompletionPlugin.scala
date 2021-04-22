package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.core.model.document.BaseUnit
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.metamodel.domain.UnionNodeMappingModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.UnionNodeMapping
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.AmfImplicits.AlsLexicalInformation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TypeDiscriminatorCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "TypeDiscriminatorCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      unbox(request)
        .map(unm =>
          request.baseUnit match {
            case d: Dialect => extractUnionTypes(unm, d).map(RawSuggestion(_, isAKey = false))
            case _          => Seq.empty
        })
        .getOrElse(Seq.empty)
    }

  private def unbox(request: AmlCompletionRequest): Option[UnionNodeMapping] =
    request.amfObject match {
      case unm: UnionNodeMapping if (inTypeDiscriminator(request.fieldEntry) && request.yPartBranch.isValue) =>
        Some(unm)
      case _ => None
    }

  private def inTypeDiscriminator(field: Option[FieldEntry]): Boolean =
    field.exists(_.field == UnionNodeMappingModel.TypeDiscriminator)

  private def extractUnionTypes(unm: UnionNodeMapping, dialect: Dialect): Seq[String] = {
    unm
      .objectRange()
      .map(_.value())
      .diff(unm.typeDiscriminator().values.toSeq) // filter already defined type discriminators
      .flatMap(getNodeMappingName(_, dialect))
  }

  private def getNodeMappingName(iri: String, dialect: Dialect): Seq[String] =
    dialect
      .findNodeMapping(iri)
      .map(_.name.value())
      .toSeq
}
