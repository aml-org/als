package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.scala.model.document.Module
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.ASTPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

import scala.concurrent.Future

object AMLComponentKeyCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLComponentKeyCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(resolvedSeq(params))

  private def resolvedSeq(params: AmlCompletionRequest): Seq[RawSuggestion] = {
    if (inRoot(params.amfObject, params.actualDocumentDefinition) && params.astPartBranch.isKey) {
      params.actualDocumentDefinition
        .documents()
        .declarationsPath()
        .option()
        .map(_.split('/').last) match {
        case Some(keyDeclarations) if isSonOf(keyDeclarations, params.astPartBranch) =>
          buildDeclaredKeys(params.actualDocumentDefinition)
        case _ => Seq()
      }
    } else Seq()
  }

  private def inRoot(amfObject: AmfObject, documentDefinition: DocumentDefinition): Boolean = {
    documentDefinition
      .documents()
      .root()
      .encoded()
      .option()
      .flatMap(id => documentDefinition.declares.collectFirst({ case n: NodeMapping if id == n.id => n }))
      .exists(i => amfObject.metaURIs.contains(i.nodetypeMapping.value())) ||
    amfObject.isInstanceOf[Module]
  }

  private def buildDeclaredKeys(documentDefinition: DocumentDefinition) = {
    documentDefinition
      .documents()
      .root()
      .declaredNodes()
      .flatMap(node => node.name().option())
      .map(RawSuggestion.forObject(_, "unknown"))
  }

  private def isSonOf(keyDeclaration: String, astPartBranch: ASTPartBranch) = {
    astPartBranch.parentKey.contains(keyDeclaration)
  }
}
