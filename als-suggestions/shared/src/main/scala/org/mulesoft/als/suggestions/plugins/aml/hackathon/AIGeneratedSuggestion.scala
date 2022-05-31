package org.mulesoft.als.suggestions.plugins.aml.hackathon

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AIGeneratedSuggestion extends AMLCompletionPlugin {
  override def id: String = "AIGeneratedSuggestion"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val raw = request.baseUnit.raw.getOrElse("")
    val eof = Position(raw.length, raw)
    if (eof == request.position) // is asking at last char
      generateSuggestion(raw)
    else Future.successful(Nil)
  }

  private def generateSuggestion(raw: String): Future[Seq[RawSuggestion]] =
    aIGenerator
      .generate(raw)
      .map(generated =>
        Seq(
          RawSuggestion(generated, isAKey = false, "generated", mandatory = false, Some("Generate"), Nil)
        )
      )

  private var aIGenerator: AIGenerator = RestAIGenerator

  def withAIGenerator(aIGenerator: AIGenerator): Unit =
    this.aIGenerator = aIGenerator
}

trait AIGenerator {
  def generate(input: String): Future[String]
}

object RestAIGenerator extends AIGenerator {
  override def generate(input: String): Future[String] = ???
}
