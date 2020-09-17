package org.mulesoft.amfintegration

import amf.client.plugins.{AMFPlugin, AMFSyntaxPlugin}
import amf.core.client.ParsingOptions
import amf.core.parser.{JsonParserFactory, ParsedDocument, ParserContext, SyamlParsedDocument}
import amf.core.rdf.RdfModelDocument
import amf.core.unsafe.PlatformSecrets
import amf.plugins.syntax.SYamlSyntaxPlugin.platform
import org.mulesoft.common.io.Output
import org.yaml.model.{YComment, YDocument, YMap, YNode}
import org.yaml.parser.YamlParser
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender}

import scala.concurrent.{ExecutionContext, Future}

object AlsSyamlSyntaxPluginHacked extends AMFSyntaxPlugin with PlatformSecrets {

  override val ID = "SYaml"

  private var keepTokens = true

  def withKeepTokens(keepTokens: Boolean): Unit = {
    this.keepTokens = keepTokens
  }

  override def init()(implicit executionContext: ExecutionContext): Future[AMFPlugin] = Future { this }

  override def dependencies(): Seq[AMFPlugin] = Nil

  override def supportedMediaTypes(): Seq[String] = Seq(
    "application/yaml",
    "application/x-yaml",
    "text/yaml",
    "text/x-yaml",
    "application/json",
    "text/json",
    "application/raml",
    "text/vnd.yaml"
  )

  override def parse(mediaType: String,
                     text: CharSequence,
                     ctx: ParserContext,
                     options: ParsingOptions): Option[ParsedDocument] = {
    if (text.length() == 0) None
    else if ((mediaType == "application/ld+json" || mediaType == "application/json") && !options.isAmfJsonLdSerilization && platform.rdfFramework.isDefined) {
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
      Some(SyamlParsedDocument(document, comment))
    }
  }

  override def unparse[W: Output](mediaType: String, doc: ParsedDocument, writer: W): Option[W] = {
    doc match {
      case input: SyamlParsedDocument =>
        val ast = input.document
        render(mediaType, ast) { (format, ast) =>
          if (format == "yaml") YamlRender.render(writer, ast, expandReferences = false)
          else JsonRender.render(ast, writer, options = JsonRenderOptions().withoutNonAsciiEncode)
          Some(writer)
        }
      case input: RdfModelDocument if platform.rdfFramework.isDefined =>
        platform.rdfFramework.get.rdfModelToSyntaxWriter(mediaType, input, writer)
      case _ => None
    }
  }

  private def render[T](mediaType: String, ast: YDocument)(render: (String, YDocument) => T): T = {
    val format = getFormat(mediaType)
    render(format, ast)
  }

  private def getFormat(mediaType: String) = if (mediaType.contains("json")) "json" else "yaml"
}
