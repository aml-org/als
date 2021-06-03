package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.core.vocabulary.Namespace.XsdTypes
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{DocumentsModel, NodeMapping, PropertyMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.{
  AMLStructureCompletionsPlugin,
  BooleanSuggestions,
  EnumSuggestions,
  PropertyMappingWrapper,
  StructureCompletionPlugin
}
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{DocumentsObjectNode, MetaDialect}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MetaDialectDocumentsCompletionPlugin extends ResolveIfApplies with BooleanSuggestions with EnumSuggestions {

  lazy val documents: Map[String, NodeMapping] = DocumentsObjectNode.properties.foldLeft(Map[String, NodeMapping]())({
    case (acc, pm) =>
      getDeclaredNode(MetaDialect(), pm.objectRange().head.value())
        .map(nm => acc + (pm.name().value() -> nm))
        .getOrElse(acc)
  })

  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: DocumentsModel if applies(request) => getSuggestion(request)
      case _                                     => None
    }
  }

  def applies(request: AmlCompletionRequest): Boolean =
    request.yPartBranch.isInBranchOf("documents") &&
      (request.yPartBranch.isKey || request.fieldEntry.isDefined)

  def getSuggestion(params: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    if (params.yPartBranch.isKey) {
      suggestDocumentKeys(params.amfObject, params.yPartBranch, params.actualDialect)
    } else {
      params.fieldEntry.map(suggestValuesForDocumentKey(params.yPartBranch, _))
    }
  }

  def getDeclaredNode(dialect: Dialect, id: String): Option[NodeMapping] =
    dialect.declares.collectFirst({
      case declaration: NodeMapping if declaration.id == id => declaration
    })

  def suggestDocumentKeys(amfObject: AmfObject,
                          yPartBranch: YPartBranch,
                          dialect: Dialect): Option[Future[Seq[RawSuggestion]]] = {
    documents
      .collectFirst { case (key, mapping) if yPartBranch.parentEntryIs(key) => mapping }
      .map(a =>
        Future {
          new AMLStructureCompletionsPlugin(a.propertiesMapping(), dialect).resolve(amfObject.metaURIs.head)
      })
  }

  def suggestValuesForDocumentKey(yPartBranch: YPartBranch, fieldEntry: FieldEntry): Future[Seq[RawSuggestion]] = {
    documents.keys
      .find(k => yPartBranch.isInBranchOf(k))
      .map(key =>
        Future {
          val nm = documents.get(key)
          val optionalMapping =
            nm.flatMap(_.propertiesMapping().find(_.nodePropertyMapping().value() == fieldEntry.field.value.iri()))
          optionalMapping match {
            case Some(pm) if pm.literalRange().option().contains(XsdTypes.xsdBoolean.iri()) => booleanSuggestions
            case Some(pm) if pm.enum().nonEmpty                                             => suggestMappingWithEnum(pm)
            case _                                                                          => Seq.empty
          }
      })
      .getOrElse(Future.successful(Seq.empty))
  }
}
