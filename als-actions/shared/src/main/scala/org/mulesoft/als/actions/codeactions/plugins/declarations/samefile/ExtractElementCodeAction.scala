package org.mulesoft.als.actions.codeactions.plugins.declarations.samefile

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.language.implicitConversions

case class ExtractElementCodeAction(params: CodeActionRequestParams) extends ExtractSameFileDeclaration {
  override protected val kindTitle: CodeActionKindTitle = ExtractElementCodeAction

  override lazy val isApplicable: Boolean =
    homogeneousVendor && !spec.isRaml && amfObject.isDefined && positionIsExtracted &&
      params.definedBy != MetaDialect.dialect && appliesToDocument()

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract element to declaration: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic
}

object ExtractElementCodeAction extends CodeActionFactory with ExtractDeclarationKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractElementCodeAction(params)
}
