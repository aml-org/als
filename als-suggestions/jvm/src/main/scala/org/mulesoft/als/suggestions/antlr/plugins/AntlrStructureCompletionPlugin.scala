package org.mulesoft.als.suggestions.antlr.plugins

import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.suggestions.antlr.suggestor.{CandidatesCollection, Core}
import org.mulesoft.antlrast.ast.Parser
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.Future

class AntlrStructureCompletionPlugin(text: String, location: String, position: DtoPosition, parser: Parser[org.antlr.v4.runtime.Parser]) extends CompletionPlugin {
  def suggest(): Future[Seq[CompletionItem]] = {
    val collection = getTokensForParser

    val tokenLabels = collection.tokens.values.flatMap(_._1).toSeq

    Future.successful(tokenLabels.map(label => CompletionItem.apply(label, insertText = Some(label))))
  }

  private def getTokensForParser: CandidatesCollection = {
    try {
      parser.parse(location, text) // when text is empty antlr is failing with:  java.lang.NullPointerException: Cannot invoke "org.antlr.v4.runtime.Token.getLine()" because "stopToken" is null
      val core = new Core(parser.getParser, parser.getParser.getATN, parser.getParser.getVocabulary)
      core.collectCandidates(getCurrentCaret(core))
    } catch {
      case e: Exception =>
        Logger.debug(e.getMessage, "AntlrStructureCompletionPlugin", "getTokensForParser")
        val core = new Core(parser.getParser, parser.getParser.getATN, parser.getParser.getVocabulary)
        core.collectCandidates(0)
    }
  }

  private def getCurrentCaret(core: Core) =
    core.caretTokenForPosition(
      position.line + 1, // starts at 1 instead of 0 as we do with DtoPosition
      position.column
    ) + 1
}


// todo: check with AMF if they can fix the grammar so the grammar to a name is not the same as the type
// it is really troublesome for us to have `ident | KEYWORD` as a name identifier because we suggest every
//   KEYWORD as if it were a name, which should be no literal at all
object AMFIgnoredRules {
  def protobuf: Seq[String] = Seq(
    "messageName",
    "enumName",
    "fieldName",
    "oneofName",
    "mapName",
    "serviceName",
    "rpcName",
  )

  def all: Seq[String] = protobuf
}