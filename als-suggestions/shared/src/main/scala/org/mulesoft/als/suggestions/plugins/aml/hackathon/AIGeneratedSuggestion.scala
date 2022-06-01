package org.mulesoft.als.suggestions.plugins.aml.hackathon

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionStructure}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler
import org.mulesoft.lexer.SourceLocation
import org.yaml.model.{ParseErrorHandler, SyamlException, YMap, YMapEntry, YNode, YPart, YSequence}
import org.yaml.parser.{JsonParser, YamlParser}
import org.yaml.render.YamlRender.render
import scalaj.http.{Http, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AIGeneratedSuggestion extends AMLCompletionPlugin {
  override def id: String = "AIGeneratedSuggestion"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val raw = request.baseUnit.raw.getOrElse("")
    val eof = Position(raw.length, raw)
    if (eof == request.position) // only suggest when standing at last char
      generateSuggestion(raw, request)
    else Future.successful(Nil)
  }

  private def generateSuggestion(raw: String, request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    aIGenerator
      .generate(raw)
      .map(generated => extractNode(raw + generated, request))
      .map { yPartBranch =>
        // ugly, always use 2 spaces for indentation!!
        // don't try to do inflow or anything of the sorts
        // just yaml!!
        val string = render(yPartBranch.node, yPartBranch.stack.count(_.isInstanceOf[YMap]) * 2)
        Seq(
          RawSuggestion(string, isAKey = false, "generated", mandatory = false, Some("Generate"), Nil)
            .withYPart(yPartBranch.node)
        )
      }

  private var aIGenerator: AIGenerator = RestAIGenerator // mutable in order to test easy

  def withAIGenerator(aIGenerator: AIGenerator): Unit =
    this.aIGenerator = aIGenerator

  private def extractNode(whole: String, request: AmlCompletionRequest): YPartBranch = {
    val handler = new ParseErrorHandler {
      override def handle(location: SourceLocation, e: SyamlException): Unit = {
        // ignore
      }
    }
    val part: YPart = YSequence(YamlParser(whole)(handler).parse(false))
    NodeBranchBuilder.build(part, request.yPartBranch.position, request.yPartBranch.isJson)
  }

}

trait AIGenerator {
  def generate(input: String): Future[String]
}

object RestAIGenerator extends AIGenerator {
  private val superSecretKey: String = "" // TODO: fillme

  override def generate(input: String): Future[String] = {
    // TODO: make me a valid json
    val data = s"""{"prompt": "$input", "temperature": 0, "max_tokens": 100}""" // dont stress me

    val result = Http("https://api.openai.com/v1/engines/text-davinci-002/completions")
      .postData(data)
      .header("Content-Type", "application/json")
      .header("Authorization", s"Bearer $superSecretKey")
      .asString

    assert(result.code == 200)
    Future.successful(extractText(result))
  }
  // this is not the method you are looking for
  private def extractText(result: HttpResponse[String]) = {
    JsonParser(result.body)
      .parse(false)
      .head
      .children
      .last
      .children
      .last
      .children
      .last
      .children
      .last
      .children
      .last
      .children
      .last
      .children
      .last
      .children
      .head
      .children
      .last
      .toString
  }
}
