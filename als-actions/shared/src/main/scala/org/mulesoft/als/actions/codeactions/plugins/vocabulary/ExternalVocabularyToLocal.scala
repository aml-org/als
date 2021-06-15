package org.mulesoft.als.actions.codeactions.plugins.vocabulary

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.External
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.ParserRangeImplicits.RangeImplicit
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTERNAL_VOCABULARY_TO_LOCAL,
  BEGIN_SYNTHESIZE_VOCABULARY,
  END_EXTERNAL_VOCABULARY_TO_LOCAL,
  END_SYNTHESIZE_VOCABULARY,
  MessageTypes
}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

class ExternalVocabularyToLocal(protected val params: CodeActionRequestParams) extends CodeActionResponsePlugin {

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  val dialect: Option[Dialect] = params.bu match {
    case d: Dialect => Some(d)
    case _          => None
  }

  val external: Option[External] =
    dialect.flatMap(_.externals.find(_.annotations.range().map(_.toPositionRange).exists(_.contains(params.range))))

  override val isApplicable: Boolean = dialect.isDefined && external.isDefined

  override protected def code(params: CodeActionRequestParams): String =
    "External vocabulary to local"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes =
    BEGIN_EXTERNAL_VOCABULARY_TO_LOCAL

  override protected def endType(params: CodeActionRequestParams): MessageTypes =
    END_EXTERNAL_VOCABULARY_TO_LOCAL

  override protected def msg(params: CodeActionRequestParams): String =
    s"External vocabulary to Local: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] =
    new ExternalVocabularyToLocalAction(dialect.get, external.get, params).task()

}

object ExternalVocabularyToLocalCodeAction extends CodeActionFactory with ExternalVocabularyToLocalKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExternalVocabularyToLocal(params)
}
