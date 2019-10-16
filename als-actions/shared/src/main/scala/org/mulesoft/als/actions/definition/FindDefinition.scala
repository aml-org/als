package org.mulesoft.als.actions.definition

import amf.core.annotations.SourceAST
import amf.core.model.document.{BaseUnit, Document}
import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{FileUtils, NodeBranchBuilder, YPartBranch}
import org.mulesoft.lsp.common.{Location, LocationLink}
import org.yaml.model.YNode.MutRef
import org.yaml.model.{YDocument, YNode, YScalar}
import org.mulesoft.lsp.convert.LspRangeConverter

trait FindDefinition {

  //TODO: Extract with regex?
  private def extractPath(raw: Option[String], position: Position): Seq[Location] = Nil

  def extractProtocol(relative: String): String = {
    val Pattern = "^([a-z]*)://.*".r
    relative.toLowerCase() match {
      case Pattern(protocol) =>
        if (protocol == "file")
          if (relative.toLowerCase.startsWith("file:///")) protocol // if absolute, I pass it as it comes
          else ""                                                   // if relative, I will recalculate full route
        else protocol
      case _ => ""
    }
  }

  private def valueToUri(root: String, relative: String, platform: Platform) = {
    val protocol = extractProtocol(relative)
    if (!protocol.isEmpty)
      relative
    else {
      val path = FileUtils.getPath(root.substring(0, root.lastIndexOf('/')), platform) + "/" + relative
        .stripPrefix("file://")
        .stripPrefix("/")
      FileUtils.getEncodedUri(path, platform)
    }
  }

  def getDefinition(bu: BaseUnit, position: Position, platform: Platform): Seq[Location] = {
    val yPartBranch: YPartBranch = {
      val ast = bu match {
        case d: Document =>
          d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
        case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
      }

      NodeBranchBuilder.build(ast.getOrElse(YDocument(IndexedSeq.empty, "")), position)
    }

    yPartBranch.node match {
      case mutRef: MutRef =>
        mutRef.target
          .map(
            target =>
              Location(
                target.location.sourceName,
                LspRangeConverter.toLspRange(PositionRange(
                  Position(target.location.lineFrom, target.location.columnFrom, zeroBased = false),
                  Position(target.location.lineTo, target.location.columnTo, zeroBased = false)
                ))
            ))
          .toSeq
      case y: YNode =>
        y.value match {
          case scalar: YScalar =>
            Seq(
              Location(valueToUri(bu.location().getOrElse(""), scalar.value.toString, platform),
                       LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0)))))
          case _ => extractPath(bu.raw, position)
        }
      case _ => extractPath(bu.raw, position)
    }
  }
}
