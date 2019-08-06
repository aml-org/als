package org.mulesoft.als.suggestions.plugins.headers

import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.configuration.Configuration
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin

import scala.concurrent.Future

object AMLHeadersCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "AMLHeadersCompletionPlugin"

  lazy val allHeaders: Seq[String] = AMLPlugin.registry
    .allDialects()
    .filterNot(d => Configuration.internalDialects.contains(d.id))
    .filterNot(_.documents().keyProperty().value())
    .flatMap(_.allHeaders)
    .map(h => s"#$h")
    .distinct

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      if (!params.uri.toLowerCase().endsWith(".json"))
        allHeaders
          .map(h => RawSuggestion(h, h, s"Define a ${h.substring(1)} file", Seq(), false, ""))
      else Seq()
    )
}
