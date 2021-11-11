package org.mulesoft.als.suggestions.plugins.headers

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.configuration.Configuration
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.amfintegration.amfconfiguration.AmfParseContext
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}

import scala.concurrent.Future

object AMLHeadersCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "AMLHeadersCompletionPlugin"

  def allHeaders(amfConfiguration: AmfParseContext): Seq[String] =
    (amfConfiguration.state.allDialects
      .filterNot(d => Configuration.internalDialects.contains(d.id))
      .filterNot(d => Option(d.documents()).exists(_.keyProperty().value())) ++ Seq(MetaDialect.dialect,
                                                                                    VocabularyDialect.dialect))
      .flatMap(computeHeaders)
      .distinct

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      if (!params.uri.toLowerCase().endsWith(".json"))
        allHeaders(params.parseContext)
          .map(h => RawSuggestion.plain(h, s"Define a ${h.substring(1)} file"))
      else Seq()
    )

  private def computeHeaders(dialect: Dialect) = {

    Seq(s"#%${dialect.nameAndVersion()}") ++
      Option(dialect.documents())
        .flatMap(d => Option(d.library()))
        .map(_ => s"#%Library / ${dialect.nameAndVersion()}") ++
      Option(dialect.documents())
        .map(_.fragments())
        .getOrElse(Seq.empty)
        .map { fragment =>
          s"#%${fragment.documentName().value()} / ${dialect.nameAndVersion()}"
        } ++
      Option(s"#%Patch / ${dialect.nameAndVersion()}")
  }
}
