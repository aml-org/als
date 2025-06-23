package org.mulesoft.als.suggestions.plugins.headers

import org.mulesoft.als.configuration.Configuration
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.amfintegration.amfconfiguration.{AmfParseContext, DocumentDefinition}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}

import scala.concurrent.Future

object AMLHeadersCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "AMLHeadersCompletionPlugin"

  def allHeaders(amfConfiguration: AmfParseContext): Seq[String] =
    (amfConfiguration.state.allDefinitions
      .filterNot(d => Configuration.internalDialects.contains(d.baseUnit.id))
      .filterNot(d => Option(d.documents()).exists(_.keyProperty().value())) ++ Seq(
      DocumentDefinition(MetaDialect.dialect),
      DocumentDefinition(VocabularyDialect.dialect)
    ))
      .flatMap(computeHeaders)
      .distinct

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      if (!params.uri.toLowerCase().endsWith(".json"))
        allHeaders(params.parseContext)
          .map(h => RawSuggestion.plain(h, s"Define a ${h.substring(1)} file"))
      else Seq()
    )

  private def computeHeaders(documentDefinition: DocumentDefinition) =
    Seq(s"#%${documentDefinition.nameAndVersion()}") ++
      Option(documentDefinition.documents())
        .flatMap(d => Option(d.library()))
        .map(_ => s"#%Library / ${documentDefinition.nameAndVersion()}") ++
      Option(documentDefinition.documents())
        .map(_.fragments())
        .getOrElse(Seq.empty)
        .map { fragment =>
          s"#%${fragment.documentName().value()} / ${documentDefinition.nameAndVersion()}"
        } ++
      Option(s"#%Patch / ${documentDefinition.nameAndVersion()}")
}
