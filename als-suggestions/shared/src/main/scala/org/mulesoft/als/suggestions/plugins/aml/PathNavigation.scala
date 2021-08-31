package org.mulesoft.als.suggestions.plugins.aml

import amf.client.parse.DefaultParserErrorHandler
import amf.core.client.ParsingOptions
import amf.core.parser.{ParserContext, SyamlParsedDocument}
import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.AmfInstance
import org.yaml.model.{YMap, YNode, YSequence, YType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PathNavigation(fullUrl: String,
                          platform: Platform,
                          env: Environment,
                          prefix: String,
                          amfInstance: AmfInstance)
    extends PathCompletion {

  private val (filePath, navPath) =
    fullUrl.split("#").toList match {
      case head :: tail => (head, tail.headOption.getOrElse(""))
      case _            => ("", "")
    }

  def suggest(): Future[Seq[RawSuggestion]] = {
    val prev =
      if (prefix.endsWith("/")) prefix
      else if (prefix.contains("/")) prefix.substring(0, prefix.lastIndexOf("/") + 1)
      else prefix + "/"

    nodes().map(nodes => nodes.map(n => RawSuggestion(s"$prev$n", isAKey = false)))
  }

  private def nodes(): Future[Seq[String]] = {
    val keys = navPath.split('/').filterNot(_.isEmpty)
    resolveRootNode().map { n =>
      n.map(r => matchNode(keys.toList, r))
        .getOrElse(Nil)
    }
  }

  def matchNode(list: List[String], node: YNode): Seq[String] = {
    val map = nodeNames(node)
    list match {
      case Nil => map.keys.toSeq
      case head :: tail =>
        map
          .get(head)
          .map(matchNode(tail, _))
          .getOrElse(Seq.empty)
    }
  }

  private def nodeNames(node: YNode): Map[String, YNode] =
    node.tagType match {
      case YType.Map => node.as[YMap].entries.flatMap(e => e.key.asScalar.map(t => (t.text, e.value))).toMap
      case YType.Seq => node.as[YSequence].nodes.zipWithIndex.map(t => (t._2.toString, t._1)).toMap
      case _         => Map.empty
    }

  def resolveRootNode(): Future[Option[YNode]] =
    platform.fetchContent(filePath, amfInstance.amfConfiguration.withResourceLoaders(env.loaders.toList)).map { c =>
      val mime = c.mime.orElse(
        platform
          .extension(filePath)
          .flatMap(platform.mimeFromExtension))
      mime.flatMap(pluginForMime) match {
        case Some(plugin) =>
          plugin
            .parse(mime.get, c.stream, ParserContext(eh = DefaultParserErrorHandler()), ParsingOptions())
            .collect({ case s: SyamlParsedDocument => s.document.node })
        case _ => None
      }
    }
}
