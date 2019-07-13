package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model.YPart

import scala.concurrent.Future

class AMLEnumCompletions(params: CompletionParams, ast: Option[YPart], yPartBranch: YPartBranch)
    extends AMLSuggestionsHelper {

  def presentArray(value: String): String =
    s"\n${getIndentation(params.currentBaseUnit, params.position)}- $value"

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
        .map(s =>
          new RawSuggestion {
            override def newText: String = s

            override def displayText: String = s

            override def description: String = s

            override def textEdits: Seq[TextEdit] = Seq()

            override def whiteSpacesEnding: String = ""
        }))
}

object AMLEnumCompletionPlugin extends CompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    val ast = params.currentBaseUnit match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }

    ast.map(NodeBranchBuilder.build(_, params.position)) match {
      case Some(yPart: YPartBranch) if yPart.isInArray || !yPart.isKey =>
        new AMLEnumCompletions(params, ast, yPart).resolve()
      case _ => Future.successful(Nil)
    }
  }
}
