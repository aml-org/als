package org.mulesoft.als.suggestions.antlr

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.suggestions.antlr.plugins.grpc.GRPCSnippetsCompletionPlugin
import org.mulesoft.als.suggestions.antlr.plugins.{AntlrStructureCompletionPlugin, CompletionPlugin}
import org.mulesoft.als.suggestions.interfaces.CompletionProvider
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.amfintegration.amfconfiguration.executioncontext.Implicits.global
import org.mulesoft.antlrast.ast.Parser
import org.mulesoft.antlrast.platform.{PlatformGraphQLParser, PlatformProtobuf3Parser}
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.collection.mutable
import scala.concurrent.Future

class JvmAntlrCompletionProvider(
                                  val baseUnit: BaseUnit,
                                  val position: DtoPosition,
                                  val parser: Parser[org.antlr.v4.runtime.Parser],
                                  val alsConfigurationState: ALSConfigurationState) extends CompletionProvider {

  override def suggest(): Future[Seq[CompletionItem]] = {
    val completionPlugins = mutable.Queue[CompletionPlugin]()
    // when this scale, make a plugin registry per grammar and try to use the generic interface for CompletionPlugins such as with AML plugins
    for {
      // todo: analyse if we should trim the file for the parser or send it complete (pros/cons)
      content <- baseUnit.raw.map(r => r.substring(0, position.offset(r)))
      location <- baseUnit.location()
    } yield {
      completionPlugins.enqueue(GRPCSnippetsCompletionPlugin)
      completionPlugins.enqueue(new AntlrStructureCompletionPlugin(content, location, position, parser))
    }
    Future.sequence(completionPlugins.map(_.suggest())).map(_.flatten)
  }
}

object PlatformAntlrCompletionProvider {
  def apply(baseUnit: BaseUnit, position: DtoPosition, alsConfigurationState: ALSConfigurationState): CompletionProvider = {
    val parser =
      if(baseUnit.location().exists(_.endsWith(".proto")))
        new PlatformProtobuf3Parser() // create parser matcher for graphql support, ideally this should already come with AMF as a "definedBy" in the parse result?
      else new PlatformGraphQLParser()
    new JvmAntlrCompletionProvider(baseUnit, position, parser, alsConfigurationState)
  }
}