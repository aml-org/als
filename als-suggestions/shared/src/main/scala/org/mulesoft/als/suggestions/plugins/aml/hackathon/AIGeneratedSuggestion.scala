package org.mulesoft.als.suggestions.plugins.aml.hackathon

import io.circe.generic.auto._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.hackathon.RestAIGenerator.OpenGptResponse
import org.mulesoft.lexer.SourceLocation
import org.yaml.model._
import org.yaml.parser.YamlParser
import org.yaml.render.JsonRender
import org.yaml.render.YamlRender.render
import scalaj.http.{Http, HttpOptions, HttpResponse}
import sttp.client3.circe._
import sttp.client3.{UriContext, basicRequest}
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, MILLISECONDS}

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
      .map(createSuggestion)

  private def createSuggestion(yPartBranch: YPartBranch): Seq[RawSuggestion] = {
    // ugly, always use 2 spaces for indentation!!
    // don't try to do inflow or anything of the sorts
    // just yaml!!
    val string = render(yPartBranch.node, yPartBranch.stack.count(_.isInstanceOf[YMap]) * 2)
    if (string.isEmpty) Nil
    else
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

  override def generate(input: String): Future[String] = Future {
    val params = buildRequestData(input)
    val data   = JsonRender.render(params, 0)
    val result: HttpResponse[String] = Http("https://api.openai.com/v1/engines/text-davinci-002/completions")
      .postData(data)
      .header("Content-Type", "application/json")
      .header("Authorization", s"Bearer $superSecretKey")
      .option(HttpOptions.readTimeout(10000))
      .asString
    if (result.is2xx) {
      decode[OpenGptResponse](result.body) match {
        case Left(error) =>
          println("**** ERROR ****")
          println(error)
          ""
        case Right(value) => extractText(value)
      }
    } else {
      println("**** ERROR ****")
      println(result)
      ""
    }
  }

  private def buildRequestData(input: String) =
    YMap(
      SourceLocation(""),
      IndexedSeq(
        YMapEntry(YNode("prompt"), YNode(input)),
        YMapEntry(YNode("temperature"), YNode(0)),
        YMapEntry(YNode("max_tokens"), YNode(100))
      )
    )

  // this is not the method you are looking for
  private def extractText(result: OpenGptResponse) = result.choices.head.text

  case class OpenGptResponse(
      id: String,
      `object`: String,
      created: Long,
      model: String,
      choices: List[OpenGptResponseChoice]
  )
  case class OpenGptResponseChoice(text: String, index: Int, logprobs: Option[String], finish_reason: String)
}
