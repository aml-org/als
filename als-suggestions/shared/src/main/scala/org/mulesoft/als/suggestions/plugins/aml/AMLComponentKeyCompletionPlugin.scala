package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMappable, NodeMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YMapEntry
import org.mulesoft.amfmanager.AmfImplicits._

import scala.concurrent.Future

object AMLComponentKeyCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLComponentKeyCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(resolvedSeq(params))

  private def resolvedSeq(params: AmlCompletionRequest): Seq[RawSuggestion] = {
    if (inRoot(params.amfObject, params.actualDialect) && params.yPartBranch.isKey) {
      params.actualDialect
        .documents()
        .declarationsPath()
        .option()
        .map(_.split('/').last) match {
        case Some(keyDeclarations) if isSonOf(keyDeclarations, params.yPartBranch) =>
          buildDeclaredKeys(params.actualDialect)
        case _ => Seq()
      }
    } else Seq()
  }

  private def inRoot(amfObject: AmfObject, dialect: Dialect): Boolean =
    dialect
      .documents()
      .root()
      .encoded()
      .option()
      .flatMap(id => dialect.declares.collectFirst({ case n: NodeMapping if id == n.id => n }))
      .exists(i => amfObject.metaURIs.contains(i.nodetypeMapping.value()))

  private def buildDeclaredKeys(dialect: Dialect) = {
    dialect
      .documents()
      .root()
      .declaredNodes()
      .flatMap(node => node.name().option())
      .map(RawSuggestion.forObject(_, "unknown"))
  }

  private def isSonOf(keyDeclaration: String, yPartBranch: YPartBranch) = {
    yPartBranch.ancestorOf(classOf[YMapEntry]) match {
      case Some(e) => e.key.asScalar.exists(_.text == keyDeclaration)
      case _       => false
    }
  }
}
