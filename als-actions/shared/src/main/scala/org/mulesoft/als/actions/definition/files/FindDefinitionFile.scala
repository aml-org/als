package org.mulesoft.als.actions.definition.files

import java.net.{URI, URISyntaxException}

import amf.core.annotations.SourceAST
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.document.{BaseUnit, Document}
import amf.core.remote.Platform
import org.mulesoft.als.common._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfmanager.dialect.DialectKnowledge
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.common.LocationLink
import org.mulesoft.lsp.convert.LspRangeConverter
import org.yaml.model.YNode.MutRef
import org.yaml.model.{YDocument, YMapEntry, YNode, YScalar}

trait FindDefinitionFile extends DialectKnowledge {

  /**
    * Proof of concept.
    * In case of wanting to analyze URIs you can uncomment this block (if used, please beautify)
    *
    * Check if the value is JUST the URI?
    *
    * @param raw BaseUnits raw
    * @param position
    * @return
    */
  private def extractPath(raw: Option[String], position: Position): Seq[LocationLink] = {
    // TODO: Extract with regex? (position?)
    Nil
    //    raw.flatMap(content => {
    //      val offset = position.offset(content)
    //      val left = content.substring(0, offset)
    //      val right = content.substring(offset)
    //      val leftWord = left.indexOf("\n") max left.indexOf(" ") max 0
    //      val rWord = right.indexOf("\n") min right.indexOf(" ")
    //      val rightWord = if (rWord < 0) right.length else rWord
    //      val sub = content.substring(leftWord, leftWord + rightWord)
    //      if (!extractProtocol(sub).isEmpty) Some(sub)
    //      else None
    //    }).map(path => Seq(Location(path, LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))))))
    //      .getOrElse(Nil)
  }

  /**
    *
    * @param relative
    * @return scheme of URI, except file:// (relative paths) which return empty
    */
  protected def extractProtocol(relative: String): String =
    try {
      val uri = new URI(relative)
      Option(uri.getScheme)
        .map {
          case "file" if !uri.getPath.startsWith("/") => ""
          case a                                      => a
        }
        .getOrElse("")
    } catch {
      case _: URISyntaxException =>
        ""
    }

  private def valueToUri(root: String, relative: String, platform: Platform): String = {
    if (relative.startsWith("#"))
      root // TODO: where to seek position
    else if (!extractProtocol(relative).isEmpty)
      relative
    else
      FileUtils.getEncodedUri(FileUtils.getPath(root.substring(0, root.lastIndexOf('/')), platform) + "/" +
                                FileUtils.getPath(relative, platform).stripPrefix("/"),
                              platform)
  }

  def getDefinitionFile(bu: BaseUnit, position: Position, platform: Platform): Seq[LocationLink] = {
    val yPartBranch: YPartBranch = {
      val ast = bu match {
        case d: Document =>
          d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
        case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
      }

      NodeBranchBuilder.build(ast.getOrElse(YDocument(IndexedSeq.empty, bu.location().getOrElse(""))), position)
    }

    yPartBranch.node match {
      case alias: YNode.Alias => Seq(locationToLsp(alias.location, alias.target.location, platform))
      case mutRef: MutRef if mutRef.target.isDefined =>
        mutRef.target
          .map(target => locationToLsp(mutRef.location, target.location, platform))
          .toSeq
      case y: YNode if appliesReference(bu, yPartBranch) =>
        y.value match {
          case scalar: YScalar if scalar.value.toString.startsWith("#") =>
            checkBaseUnitForRef(yPartBranch, ObjectInTreeBuilder.fromUnit(bu, position), platform)
          case scalar: YScalar => // TODO => DocumentLink
            Seq(
              LocationLink(
                valueToUri(scalar.location.sourceName, scalar.value.toString, platform),
                LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
                LspRangeConverter.toLspRange(PositionRange(Position(0, 0), Position(0, 0))),
                Some(sourceLocationToRange(scalar.location))
              ))
          case _ => extractPath(bu.raw, position)
        }
      case _ => extractPath(bu.raw, position)
    }
  }

  private def locationToLsp(sourceLocation: SourceLocation,
                            targetLocation: SourceLocation,
                            platform: Platform): LocationLink = {
    LocationLink(
      targetLocation.sourceName,
      sourceLocationToRange(targetLocation),
      sourceLocationToRange(targetLocation),
      Some(sourceLocationToRange(sourceLocation))
    )
  }

  private def sourceLocationToRange(targetLocation: SourceLocation) = {
    LspRangeConverter.toLspRange(
      PositionRange(
        Position(targetLocation.lineFrom, targetLocation.columnFrom, zeroBased = targetLocation.isZero).asZeroBased,
        Position(targetLocation.lineTo, targetLocation.columnTo, zeroBased = targetLocation.isZero).asZeroBased
      ))
  }

  protected def isInUsesRef(yPartBranch: YPartBranch): Boolean = {
    yPartBranch.isValue && {
      val stack = yPartBranch.stack.iterator
      stack.hasNext && {
        stack.next match {
          case _: YMapEntry =>
            stack.drop(2)
            stack.hasNext && {
              stack.next match {
                case entry: YMapEntry =>
                  entry.key.value.toString == "uses" && {
                    stack.drop(3)
                    !stack.hasNext
                  }
                case _ => false
              }
            }
          case _ => false
        }
      }
    }
  }

  private def checkBaseUnitForRef(yPartBranch: YPartBranch,
                                  objectInTree: ObjectInTree,
                                  platform: Platform): Seq[LocationLink] =
    objectInTree.obj.fields
      .entry(LinkableElementModel.Target)
      .flatMap(
        fe =>
          fe.value.value.annotations
            .find(classOf[SourceAST])
            .map(sast => locationToLsp(yPartBranch.node.location, sast.ast.location, platform)))
      .toSeq

  private def appliesReference(bu: BaseUnit, yPartBranch: YPartBranch): Boolean =
    dialectFor(bu).exists(dialect => isInclusion(yPartBranch, dialect)) || isInUsesRef(yPartBranch)
}
