package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.client.scala.errorhandling.DefaultErrorHandler
import amf.core.client.scala.parse.document.{ParserContext, SyamlParsedDocument}
import amf.core.internal.parser.ParseConfig
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.yaml.model.{YMap, YNode, YSequence, YType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[pathnavigation] case class PathNavigation(
    fileUri: String,
    navPath: String,
    prefix: String,
    alsConfiguration: ALSConfigurationState,
    containsHashtag: Boolean
) extends PathCompletion
    with PathSuggestor {

  override def suggest(): Future[Seq[RawSuggestion]] = {
    if (!containsHashtag)
      nodes().flatMap(nodes => PathSuggestorHashtag(nodes, prefix).suggest())
    else nodes().map(nodes => buildSuggestions(nodes, prefix))
  }

  private def nodes(): Future[Seq[String]] = {
    val lastSegment         = navPath.lastIndexOf("/")
    val allSegmentsTillLast = if (lastSegment > 0) navPath.substring(0, lastSegment) else ""
    val keys                = allSegmentsTillLast.split('/').filterNot(_.isEmpty)
    resolveRootNode().map { n =>
      n.map(r => matchNode(keys.toList, r))
        .getOrElse(Nil)
    }
  }

  private def matchNode(list: List[String], node: YNode): Seq[String] = {
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

  private def resolveRootNode(): Future[Option[YNode]] =
    alsConfiguration
      .fetchContent(fileUri)
      .map { c =>
        val mime = c.mime.orElse(
          alsConfiguration.platform
            .extension(fileUri)
            .flatMap(alsConfiguration.platform.mimeFromExtension)
        )
        mime.flatMap(pluginForMime) match {
          case Some(plugin) =>
            plugin
              .parse(
                c.stream,
                mime.get,
                ParserContext(config = ParseConfig(alsConfiguration.getAmfConfig, DefaultErrorHandler()))
              ) match {
              case SyamlParsedDocument(document, _) => Some(document.node)
              case _                                => None
            }
          case _ => None
        }
      }
      .recoverWith { case _ => // wildcard to avoid unnecessary errors in suggestions - W-14277188/W-14279644
        Future.successful(None)
      }
}
