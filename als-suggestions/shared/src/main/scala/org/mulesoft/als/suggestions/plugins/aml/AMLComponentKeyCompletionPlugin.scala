package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YMapEntry

import scala.concurrent.Future

object AMLComponentKeyCompletionPlugin extends AMLCompletionPlugin with AMLSuggestionsHelper {
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
          buildDeclaredKeys(params.actualDialect, "\n" + getIndentation(params.baseUnit, params.position))
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
      .exists(i => amfObject.meta.`type`.exists(_.iri() == i))

  private def buildDeclaredKeys(dialect: Dialect, indentation: String) = {
    dialect
      .documents()
      .root()
      .declaredNodes()
      .flatMap(node => node.name().option())
      .map(RawSuggestion(_, indentation, isAKey = true))
  }

  private def isSonOf(keyDeclaration: String, yPartBranch: YPartBranch) = {
    yPartBranch.ancestorOf(classOf[YMapEntry]) match {
      case Some(e) => e.key.asScalar.exists(_.text == keyDeclaration)
      case _       => false
    }
  }
}
