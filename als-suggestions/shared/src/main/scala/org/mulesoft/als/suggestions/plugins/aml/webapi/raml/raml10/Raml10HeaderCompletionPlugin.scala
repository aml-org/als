package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Raml10HeaderCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Raml10HeaderCompletionPlugin"

  private lazy val headers: Seq[String] = Seq("SecurityScheme",
                                              "DocumentationItem",
                                              "ResourceType",
                                              "Library",
                                              "Overlay",
                                              "Trait",
                                              "NamedExample",
                                              "AnnotationTypeDeclaration",
                                              "DataType",
                                              "Extension")

  override def resolve(
      params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      // if I'm here, I'm RAML, verify that I am after the definition
      if (params.position.line <= 1 && params.baseUnit.raw
            .exists(r =>
              r.substring(0, 0 max params.position.column)
                .startsWith("#%RAML 1.0")))
        if (params.baseUnit.raw
              .exists(_.charAt(params.position.column - 1) == ' ')) // check if I already have whitespace
          headers.map(h => RawSuggestion.apply(h, isAKey = false))
        else
          headers.map(h => RawSuggestion.apply(s" $h", isAKey = false))
      else Seq()
    }
}
