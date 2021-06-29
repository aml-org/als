package org.mulesoft.amfintegration

import amf.core.client.common.{HighPriority, PluginPriority}
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.parse.document.{ParsedDocument, ParserContext, SyamlParsedDocument}
import amf.core.internal.parser.domain.JsonParserFactory
import amf.core.internal.plugins.syntax.SyamlSyntaxParsePlugin
import amf.core.internal.unsafe.PlatformSecrets
import org.yaml.model.{YComment, YDocument, YMap, YNode}
import org.yaml.parser.YamlParser

object AlsSyamlSyntaxPluginHacked extends AMFSyntaxParsePlugin with PlatformSecrets {

  override def priority: PluginPriority = HighPriority

  override val id: String = s"als-${SyamlSyntaxParsePlugin.id}"

  private var keepTokens = true
  def withKeepTokens(keepTokens: Boolean): Unit =
    this.keepTokens = keepTokens

  override def mediaTypes(): Seq[String] = SyamlSyntaxParsePlugin.mediaTypes

  override def mainMediaType: String = SyamlSyntaxParsePlugin.mainMediaType

  override def parse(text: CharSequence, mediaType: String, ctx: ParserContext): ParsedDocument = {
    if (text.length() == 0) SyamlParsedDocument(YDocument(YNode.Null))
    else if ((mediaType == "application/ld+json" || mediaType == "application/json") &&
             !ctx.parsingOptions.isAmfJsonLdSerialization &&
             platform.rdfFramework.isDefined) {
      platform.rdfFramework.get.syntaxToRdfModel(mediaType, text)
    } else {
      val parser = getFormat(mediaType) match {
        case "json" => JsonParserFactory.fromCharsWithSource(text, ctx.rootContextDocument)(ctx.eh)
        case _      => YamlParser(text, ctx.rootContextDocument)(ctx.eh).withIncludeTag("!include")
      }
      val document1 = parser.document(keepTokens)
      val (document, comment) = document1 match {
        case d if d.isNull =>
          (YDocument(Array(YNode(YMap.empty)), ctx.rootContextDocument), d.children collectFirst {
            case c: YComment => c.metaText
          })
        case d =>
          (d, d.children collectFirst { case c: YComment => c.metaText })
      }
      SyamlParsedDocument(document, comment)
    }
  }

  private def getFormat(mediaType: String) = if (mediaType.contains("json")) "json" else "yaml"

  override def applies(element: CharSequence): Boolean = true // why not SyamlSyntaxParsePlugin.applies(element) ?
}
