package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.domain.{DocumentsModel, NodeMapping}
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace.XsdTypes
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.common.ASTPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionsPlugin, BooleanSuggestions, EnumSuggestions}
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{DocumentsObjectNode, MetaDialect}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MetaDialectDocumentsCompletionPlugin extends ResolveIfApplies with BooleanSuggestions with EnumSuggestions {

  lazy val documents: Map[String, NodeMapping] =
    DocumentsObjectNode.properties.foldLeft(Map[String, NodeMapping]())({ case (acc, pm) =>
      getDeclaredNode(DocumentDefinition(MetaDialect()), pm.objectRange().head.value())
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
    request.astPartBranch.isInBranchOf("documents") &&
      (request.astPartBranch.isKey || request.fieldEntry.isDefined)

  def getSuggestion(params: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    if (params.astPartBranch.isKey) {
      suggestDocumentKeys(params.amfObject, params.astPartBranch, params.actualDocumentDefinition)
    } else {
      params.fieldEntry.map(suggestValuesForDocumentKey(params.astPartBranch, _))
    }
  }

  def getDeclaredNode(documentDefinition: DocumentDefinition, id: String): Option[NodeMapping] =
    documentDefinition.declares.collectFirst({
      case declaration: NodeMapping if declaration.id == id => declaration
    })

  def suggestDocumentKeys(
                           amfObject: AmfObject,
                           astPart: ASTPartBranch,
                           documentDefinition: DocumentDefinition
  ): Option[Future[Seq[RawSuggestion]]] = {
    documents
      .collectFirst { case (key, mapping) if astPart.parentEntryIs(key) => mapping }
      .map(a =>
        Future {
          new AMLStructureCompletionsPlugin(a.propertiesMapping(), documentDefinition).resolve(amfObject.metaURIs.head)
        }
      )
  }

  def suggestValuesForDocumentKey(astElement: ASTPartBranch, fieldEntry: FieldEntry): Future[Seq[RawSuggestion]] = {
    documents.keys
      .find(k => astElement.isInBranchOf(k))
      .map(key =>
        Future {
          val nm = documents.get(key)
          val optionalMapping =
            nm.flatMap(_.propertiesMapping().find(_.nodePropertyMapping().value() == fieldEntry.field.value.iri()))
          optionalMapping match {
            case Some(pm) if pm.literalRange().option().contains(XsdTypes.xsdBoolean.iri()) => booleanSuggestions
            case Some(pm) if pm.enum().nonEmpty => suggestMappingWithEnum(pm)
            case _                              => Seq.empty
          }
        }
      )
      .getOrElse(Future.successful(Seq.empty))
  }
}
