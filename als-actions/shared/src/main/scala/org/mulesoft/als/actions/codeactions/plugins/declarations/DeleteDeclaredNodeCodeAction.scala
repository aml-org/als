package org.mulesoft.als.actions.codeactions.plugins.declarations

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfObject
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{FieldEntryImplicit, _}
import org.mulesoft.lexer.InputRange
import org.mulesoft.lsp.edit.{TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.{CodeAction, CodeActionKind}
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{BEGIN_DELETE_NODE_ACTION, END_DELETE_NODE_ACTION, MessageTypes}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.{YMap, YMapEntry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteDeclaredNodeCodeAction(override val params: CodeActionRequestParams, override val kind: CodeActionKind)
    extends CodeActionResponsePlugin
    with BaseDeclarableExtractors {

  override val isApplicable: Boolean = tree.exists(t => t.isDeclared() && t.fieldEntry.exists(_.isSemanticName))

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def task(params: CodeActionRequestParams): Future[Seq[CodeAction]] = {
    Future {
      tree
        .map(t => {
          val referencesEdits =
            nameLocation(t.obj).map(n => removeReferences(n, params.allRelationships)).getOrElse(Map.empty)
          val stringToEdits: Map[String, Seq[TextEdit]] = removeObj(t.obj)
            .map { ed =>
              referencesEdits.get(params.uri) match {
                case Some(seq) => referencesEdits.updated(params.uri, seq :+ ed)
                case _         => referencesEdits + (params.uri -> Seq(ed))
              }
            }
            .getOrElse(referencesEdits)
          WorkspaceEdit(stringToEdits, Nil)
        })
        .map(we => DeleteDeclaredNodeCodeAction.baseCodeAction(we))
        .toSeq
    }
  }

  private def removeObj(obj: AmfObject) = {
    existAnyDeclaration(obj)
      .map(inputToPositionRange)
      .map(r => TextEdit(r.copy(start = r.start.copy(character = 0)), ""))
  }

  private def nameLocation(obj: AmfObject): Option[Location] = {
    obj.namedField().flatMap(v => v.annotations.ast().orElse(v.value.annotations.ast()).map(p => p.yPartToLocation))
  }

  private def removeReferences(nameLocation: Location, r: Seq[RelationshipLink]): Map[String, Seq[TextEdit]] = {
    r.filter(re => {
        re.targetEntry.yPartToLocation.uri == nameLocation.uri && re.targetEntry.range.contains(
          Position(nameLocation.range.start).toAmfPosition)
      })
      .map(rl => rl.sourceEntry.location.sourceName -> TextEdit(inputToPositionRange(rl.sourceEntry.range), ""))
      .groupBy(_._1)
      .map(t => (t._1 -> t._2.map(_._2)))

  }

  private def inputToPositionRange(inputRange: InputRange) = LspRangeConverter.toLspRange(PositionRange(inputRange))

  private def existAnyDeclaration(obj: AmfObject): Option[InputRange] = {
    val others = params.bu.declarations.filterNot(_ == obj)
    if (others.isEmpty) deleteAll(obj)
    else deleteDeclarationGroup(obj)
  }

  private def deleteAll(obj: AmfObject) = {
    declarationsPath match {
      case Some(d) =>
        val flatten: Option[YMapEntry] = yPartBranch
          .map(_.stack)
          .getOrElse(Nil)
          .reverse
          .collectFirst({ case m: YMap => m.entries.find(_.key.asScalar.exists(_.text == d)) })
          .flatten
        flatten.map(_.range)
      case _ => deleteDeclarationGroup(obj)
    }
  }

  private def deleteDeclarationGroup(obj: AmfObject) = {
    obj
      .declarableKey(params.dialect)
      .flatMap { d =>
        params.bu.annotations.declarationKeys().find(_.entry.key.asScalar.exists(_.text == d))
      }
      .flatMap { dk =>
        if (dk.entry.value.as[YMap].entries.size == 1) Some(dk.entry.range)
        else None
      }
      .orElse(obj.annotations.ast().map(_.range))
  }

  private def positionToTextEdit(l: LexicalInformation) = {
    l.range
  }
  override protected def code(params: CodeActionRequestParams): String = "Delete declared node and references(cascade)"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes = BEGIN_DELETE_NODE_ACTION

  override protected def endType(params: CodeActionRequestParams): MessageTypes = END_DELETE_NODE_ACTION

  override protected def msg(params: CodeActionRequestParams): String =
    s"Delete declared node: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String = params.uri
}

object DeleteDeclaredNodeCodeAction extends CodeActionFactory {
  override val kind: CodeActionKind = CodeActionKind.Refactor
  override final val title          = "Delete declaration (Cascade)"
  override def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new DeleteDeclaredNodeCodeAction(params, kind)
}
