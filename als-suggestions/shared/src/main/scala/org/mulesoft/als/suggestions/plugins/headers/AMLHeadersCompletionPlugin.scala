package org.mulesoft.als.suggestions.plugins.headers

import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.configuration.Configuration
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

import scala.concurrent.Future

object AMLHeadersCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "AMLHeadersCompletionPlugin"

  def allHeaders: Seq[String] =
    (AMLPlugin.registry
      .allDialects()
      .filterNot(d => Configuration.internalDialects.contains(d.id))
      .filterNot(_.documents().keyProperty().value())
      .toSeq :+ MetaDialect.dialect)
      .flatMap(computeHeaders)
      .distinct

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      if (!params.uri.toLowerCase().endsWith(".json"))
        allHeaders
          .map(h => RawSuggestion.plain(h, s"Define a ${h.substring(1)} file"))
      else Seq()
    )

  private def computeHeaders(dialect: Dialect) = {

    Seq(s"#%${dialect.nameAndVersion()}") ++
      Option(dialect.documents().library()).map(_ => s"#%Library / ${dialect.nameAndVersion()}") ++
      dialect.documents().fragments().map { fragment =>
        s"#%${fragment.documentName().value()} / ${dialect.nameAndVersion()}"
      } ++
      Option(s"#%Patch / ${dialect.nameAndVersion()}")
  }
}
