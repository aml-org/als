package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Raml10HeaderCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Raml10HeaderCompletionPlugin"
  // those headers should be defined as fragments in the dialect?
  private lazy val headers: Seq[String] = Seq(
    "SecurityScheme",
    "DocumentationItem",
    "ResourceType",
    "Library",
    "Overlay",
    "Trait",
    "NamedExample",
    "AnnotationTypeDeclaration",
    "DataType",
    "Extension"
  )

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      // if I'm here, I'm RAML, verify that I am after the definition
      if (
        params.position.line == 0 && params.baseUnit.raw
          .exists(r =>
            r.substring(0, 0 max params.position.column)
              .startsWith("#%RAML 1.0")
          )
      )
        headers.map(h =>
          RawSuggestion.plain(
            s"#%RAML 1.0 $h",
            PositionRange(
              Position(0, 0),
              Position(0, params.baseUnit.raw.map(eolOrEof).getOrElse(params.position.column))
            )
          )
        )
      else Seq()
    }

  private def eolOrEof(text: String): Int = {
    if (text.indexOf('\n') <= 0) text.length
    else text.indexOf('\n')
  }
}
