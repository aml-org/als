package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.ParserRangeImplicits.RangeImplicit
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_SYNTHESIZE_VOCABULARY,
  END_SYNTHESIZE_VOCABULARY,
  MessageTypes
}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

class SynthesizeVocabulary(protected val params: CodeActionRequestParams) extends CodeActionResponsePlugin {

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  val dialect: Option[Dialect] = params.bu match {
    case d: Dialect => Some(d)
    case _          => None
  }

  override val isApplicable: Boolean = dialect.isDefined &&
    dialect.exists(_.name().annotations().range().map(_.toPositionRange).exists(_.contains(params.range)))

  override protected def code(params: CodeActionRequestParams): String =
    "Raml type to json schema code action"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes =
    BEGIN_SYNTHESIZE_VOCABULARY

  override protected def endType(params: CodeActionRequestParams): MessageTypes =
    END_SYNTHESIZE_VOCABULARY

  override protected def msg(params: CodeActionRequestParams): String =
    s"RAML type to json schema : \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] =
    new SynthesizeVocabularyAction(dialect.get, params).synthesize()

}

object SynthesizeVocabularyCodeAction extends CodeActionFactory with SynthesizeVocabularyKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new SynthesizeVocabulary(params)
}
