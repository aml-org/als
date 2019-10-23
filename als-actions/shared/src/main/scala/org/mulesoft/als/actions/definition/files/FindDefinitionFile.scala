package org.mulesoft.als.actions.definition.files

import amf.core.annotations.SourceAST
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.common._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfmanager.dialect.DialectKnowledge
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.common.LocationLink
import org.yaml.model.{YNode, YScalar}

trait FindDefinitionFile extends DialectKnowledge with ActionTools {

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
  private def extractPath(raw: Option[String], position: Position): Seq[LocationLink] =
    // TODO: Extract with regex? (position?)
    Nil

  def getDefinitionFile(bu: BaseUnit, position: Position, platform: Platform): Seq[LocationLink] = {
    val yPartBranch: YPartBranch = NodeBranchBuilder.build(bu, position.toAmfPosition)

    yPartBranch.node match {
      case alias: YNode.Alias => Seq(locationToLsp(alias.location, alias.target.location, platform))
      case y: YNode if appliesReference(bu, yPartBranch) =>
        y.value match {
          case scalar: YScalar if scalar.value.toString.startsWith("#") =>
            checkBaseUnitForRef(yPartBranch, ObjectInTreeBuilder.fromUnit(bu, position.toAmfPosition), platform)
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
}
