package org.mulesoft.als.suggestions.plugins.aml

import amf.client.parse.DefaultParserErrorHandler
import amf.core.client.ParsingOptions
import amf.core.parser.{ParserContext, SyamlParsedDocument}
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.RawSuggestion
import org.yaml.model.{YMap, YNode, YSequence, YType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PathNavigation(fullUrl: String, platform: Platform, env: Environment, prefix: String)
    extends PathCompletion {

  private val (filePath, navPath) = {
    fullUrl.split("#").toList match {
      case head :: tail => (head, tail.headOption.getOrElse(""))
      case _            => ("", "")
    }
  }

  def suggest(): Future[Seq[RawSuggestion]] = {
    val prev =
      if (prefix.endsWith("/")) prefix
      else if (prefix.contains("/")) prefix.substring(0, prefix.lastIndexOf("/") + 1)
      else prefix + "/"

    nodes().map(nodes => nodes.map(n => RawSuggestion(s"$prev$n", isAKey = false)))
  }

  private def nodes(): Future[Seq[String]] = {

    val keys = navPath.split('/').reverse
    resolveRootNode().map { n =>
      n.map(r => matchNode(keys.toList, r)).getOrElse(Nil)
    }
  }

  def matchNode(list: List[String], node: YNode): Seq[String] = {
    val map  = nodeNames(node)
    val keys = map.map(t => if (t._2.tagType == YType.Map || t._2.tagType == YType.Seq) t._1 + "/" else t._1).toSeq
    list match {
      case Nil         => keys
      case head :: Nil => keys
      case head :: tail =>
        matchNode(tail, map(head))
    }
  }

  private def nodeNames(node: YNode): Map[String, YNode] = {
    node.tagType match {
      case YType.Map => node.as[YMap].entries.flatMap(e => e.key.asScalar.map(t => (t.text, e.value))).toMap
      case YType.Seq => node.as[YSequence].nodes.zipWithIndex.map(t => (t._2.toString, t._1)).toMap
      case _         => Map.empty
    }
  }

  def resolveRootNode(): Future[Option[YNode]] = {
    platform.resolve(filePath, env).map { c =>
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
}
