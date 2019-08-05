package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}
import org.yaml.model.YPart

import scala.concurrent.Future

class AMLEnumCompletionsPlugin(params: AMLCompletionParams, ast: Option[YPart], yPartBranch: YPartBranch)
    extends AMLSuggestionsHelper {

  def presentArray(value: String): String =
    s"\n${getIndentation(params.baseUnit, params.position)}- $value"

  private def getSuggestions: Seq[String] =
    params.propertyMappings.headOption
      .map(
        pm =>
          pm.enum()
            .flatMap(_.option().map(e => {

              if (pm.allowMultiple()
                    .value() && params.prefix.isEmpty && !yPartBranch.isArray && !yPartBranch.isInArray)
                presentArray(e.toString)
              else e.toString
            })))
      .getOrElse(Nil)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .map(s => RawSuggestion(s, isAKey = false)))
}

object AMLEnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] = {
    val ast = params.baseUnit match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }

    ast.map(NodeBranchBuilder.build(_, params.position)) match {
      case Some(yPart: YPartBranch) if yPart.isInArray || !yPart.isKey =>
        new AMLEnumCompletionsPlugin(params, ast, yPart).resolve()
      case _ => Future.successful(Nil)
    }
  }
}
