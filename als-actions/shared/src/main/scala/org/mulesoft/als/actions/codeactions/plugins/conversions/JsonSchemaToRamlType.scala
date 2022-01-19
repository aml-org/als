package org.mulesoft.als.actions.codeactions.plugins.conversions

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.core.internal.remote.Spec
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.internal.annotations.ParsedJSONSchema
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon.{
  declaredElementNode,
  renderNode
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractSameFileDeclaration
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.{Range, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_JSON_SCHEMA_TO_TYPE_ACTION,
  END_JSON_SCHEMA_TO_TYPE_ACTION,
  MessageTypes
}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.{YNode, YNodePlain}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JsonSchemaToRamlType(override protected val params: CodeActionRequestParams)
    extends CodeActionResponsePlugin
    with AmfObjectResolver
    with ExtractSameFileDeclaration {

  override protected val alsConfigurationState: ALSConfigurationState = params.alsConfigurationState

  override val isApplicable: Boolean =
    params.bu.sourceSpec.contains(Spec.RAML10) &&
      maybeAnyShape.isDefined && positionIsExtracted

  protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def code(params: CodeActionRequestParams): String =
    "Json Schema to Raml Type code action"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes =
    BEGIN_JSON_SCHEMA_TO_TYPE_ACTION

  override protected def endType(params: CodeActionRequestParams): MessageTypes =
    END_JSON_SCHEMA_TO_TYPE_ACTION

  override protected def msg(params: CodeActionRequestParams): String =
    s"Json Schema to Raml Type: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri

  // we are looking for a json schema shape inside the current yPartBranch node
  def tryExtractFromTree(): Option[AnyShape] = {
    yPartBranch.map(_.node) match {
      // if we are not in a plain node, there is no way we could be on a inlined json schema
      case Some(node: YNodePlain) =>
        maybeTree.flatMap(tree => {
          val range = PositionRange(node.range)
          extractShapeFromAmfObject(
            tree.stack.find(
              obj =>
                isJsonSchemaShape(obj) &&
                  containsPosition(obj, position) &&
                  obj.annotations.lexicalInformation().exists(lex => range.contains(PositionRange(lex.range).`end`))))
        })
      case _ => None
    }
  }

  override lazy val maybeAnyShape: Option[AnyShape] = {
    extractShapeFromAmfObject(resolvedAmfObject) match {
      case Some(v) if isInlinedJsonSchema(v) => Some(v)
      case _                                 => tryExtractFromTree()
    }
  }

  def inplaceRamlTypeTextEdit(shape: AnyShape, range: Range): Future[(String, TextEdit)] =
    for {
      str <- renderRamlType(shape)
    } yield (params.uri, TextEdit(range, "\n" + str + "\n"))

  private def renderRamlType(shape: AnyShape): Future[String] = Future {
    shape.annotations.reject(_.isInstanceOf[ParsedJSONSchema])
    val node: Option[YNode] = declaredElementNode(Some(shape), params.definedBy, alsConfigurationState)
    val parent              = yPartBranch.flatMap(_.parentEntry)
    node
      .map(
        renderNode(_, parent, params.bu, params.configuration, jsonOptions, yamlOptions)
      )
      .map(_._1)
      .getOrElse("")
  }

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      RamlTypeExtractor
        .linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] = {
    (yPartBranch.map(_.node), maybeAnyShape) match {
      case (Some(entry), Some(shape)) =>
        val range = LspRangeConverter.toLspRange(PositionRange(entry.range))
        inplaceRamlTypeTextEdit(shape, range).map(edits =>
          buildEdit(edits._1, edits._2).map(JsonSchemaToRamlType.baseCodeAction))
      case _ => Future.successful(Seq.empty)
    }
  }

  def buildEdit(editUri: String, editTextEdit: TextEdit): Seq[AbstractWorkspaceEdit] =
    Seq(
      AbstractWorkspaceEdit(
        Seq(Left(TextDocumentEdit(VersionedTextDocumentIdentifier(editUri, None), Seq(editTextEdit))))))

  override protected val kindTitle: CodeActionKindTitle = JsonSchemaToRamlType

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic

}

object JsonSchemaToRamlType extends CodeActionFactory with JsonSchemaToRamlTypeKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new JsonSchemaToRamlType(params)
}
