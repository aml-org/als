package org.mulesoft.high.level.interfaces

import amf.core.annotations.SourceNode
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import common.dtoTypes.Position
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.typesystem_interfaces.IHasExtra

trait IParseResult extends IHasExtra {

  def amfNode: AmfObject

  def amfBaseUnit: BaseUnit

  def root: Option[IHighLevelNode]

  def parent: Option[IHighLevelNode]

  def setParent(node: IHighLevelNode): Unit

  def children: Seq[IParseResult]

  def isAttr: Boolean

  def asAttr: Option[IAttribute]

  def isElement: Boolean

  def asElement: Option[IHighLevelNode]

  def isUnknown: Boolean

  def property: Option[IProperty]

  def printDetails(indent: String = ""): String

  def printDetails: String = printDetails()

  def astUnit: IASTUnit

  def sourceInfo: ISourceInfo

  def getNodeByPosition(pos: Int): Option[IParseResult] =
    selectNodeWhichContainsPosition(pos).map(n => {
      val posOffset = astUnit.positionsMapper.offset(pos)
      var result = n
      if (result.sourceInfo.isYAML) {
        while (
          result.parent.isDefined
            && result.sourceInfo.valueOffset.isDefined
            && result.sourceInfo.valueOffset.get > posOffset
            && !result.sourceInfo.containsPositionInKey(pos)) {

          result = result.parent.get
        }
      }
      result
    })

  def getNodeByPosition(position: Position): Option[IParseResult] =
    selectNodeWhichContainsPosition(position)
      .map(n => {
        val posOffset = astUnit.positionsMapper.offset(position)
        var result = n
        if (result.sourceInfo.isYAML) {
          while (
            result.parent.isDefined
              && result.sourceInfo.valueOffset.isDefined
              && result.sourceInfo.valueOffset.get > posOffset
              && !result.sourceInfo.containsPositionInKey(position)) {

            result = result.parent.get
          }
        }
        result
      })

  protected def selectNodeWhichContainsPosition(pos: Int): Option[IParseResult] =
    if (sourceInfo.containsPosition(pos)) Some(this) else None

  protected def selectNodeWhichContainsPosition(position: Position): Option[IParseResult] = {
    if (sourceInfo.containsPosition(position)) Some(this) else None
  }

  def unitPath: Option[String] = {
    val opt: Option[String] = amfNode.annotations.find(classOf[SourceNode]).map(_.node.sourceName).orElse(Option(amfNode.id))
    opt match {
      case Some(str) =>
        var result = str
        val ind = str.indexOf("#")
        if (ind >= 0) {
          result = str.substring(0, ind)
        }
        Some(result)
      case _ => None
    }

  }
}
