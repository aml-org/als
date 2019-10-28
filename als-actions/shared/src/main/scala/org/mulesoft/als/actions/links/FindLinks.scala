package org.mulesoft.als.actions.links

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.ReferenceStyles
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.amfmanager.dialect.DialectKnowledge
import org.mulesoft.lsp.feature.link.DocumentLink
import org.yaml.model._

object FindLinks {

  def getLinks(bu: BaseUnit, platform: Platform): Seq[DocumentLink] = {
    lazy val ast = NodeBranchBuilder.astFromBaseUnit(bu)

    lazy val hasRamlIncludes = DialectKnowledge
      .dialectFor(bu)
      .exists(dialect => {
        Option(dialect.documents()).forall(d =>
          d.referenceStyle().is(ReferenceStyles.RAML) || d.referenceStyle().isNullOrEmpty)
      })

    lazy val hasJsonIncludes = DialectKnowledge
      .dialectFor(bu)
      .exists(dialect => {
        Option(dialect.documents()).forall(d =>
          d.referenceStyle().is(ReferenceStyles.JSONSCHEMA) || d.referenceStyle().isNullOrEmpty)
      })

    def getLink(part: YPart): Seq[DocumentLink] =
      extractUsesLinks(part, platform) ++ {
        if (hasJsonIncludes) extractJsonRefs(part, platform) else Nil
      } ++ {
        if (hasRamlIncludes) extractRamlIncludes(part, platform) else Nil
      }

    def seekLinks(ast: YPart): Seq[DocumentLink] = ast.foreach(getLink)

    ast.map(seekLinks).getOrElse(Nil)
  }

  private def isRoot(map: YMap) = map.entries.forall(_.key.location.columnFrom == 0)

  def extractUsesLinks(yPart: YPart, platform: Platform): Seq[DocumentLink] =
    yPart match {
      case map: YMap if isRoot(map) =>
        map.entries
          .find(p => p.key.asScalar.map(_.text).getOrElse("") == "uses")
          .map(uses => {
            uses.value.value match {
              case m: YMap =>
                m.entries.map(e => entryToLink(e, platform))
            }
          })
          .getOrElse(Nil)
      case _ =>
        Nil
    }

  private def entryToLink(e: YMapEntry, platform: Platform) =
    nodeToLink(e.value, platform)

  private def nodeToLink(n: YNode, platform: Platform) =
    DocumentLink(ActionTools.sourceLocationToRange(n.value.location),
                 ActionTools.valueToUri(n.location.sourceName, n.asScalar.map(_.text).getOrElse(""), platform))

  def extractJsonRefs(yPart: YPart, platform: Platform): Seq[DocumentLink] =
    yPart match {
      case entry: YMapEntry if appliesRef(entry) =>
        Seq(entryToLink(entry, platform))
      case _ => Nil
    }

  private def appliesRef(entry: YMapEntry): Boolean = {
    entry.key.asScalar.map(_.text).getOrElse("").toLowerCase == "$ref" &&
    !entry.value.asScalar.map(_.text).getOrElse("").startsWith("#")
  }

  def extractRamlIncludes(yPart: YPart, platform: Platform): Seq[DocumentLink] =
    yPart match {
      case node: YNode if node.tagType == YType.Include => Seq(nodeToLink(node, platform))
      case _                                            => Nil
    }

  implicit class ASTAddons(ast: YPart) {
    implicit def foreach[A](fn: YPart => Seq[A]): Seq[A] = {
      val sourceName = ast.sourceName

      def innerFn(yPart: YPart): Seq[A] = {
        if (yPart.sourceName == sourceName)
          fn(yPart) ++ yPart.children.flatMap(innerFn)
        else Nil
      }

      innerFn(ast)
    }
  }

}
