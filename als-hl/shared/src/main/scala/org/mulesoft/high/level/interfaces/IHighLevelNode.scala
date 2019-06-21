package org.mulesoft.high.level.interfaces

import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition}

trait IHighLevelNode extends IParseResult {
  //def types(): IParsedTypeCollection
  //def parsedType(): IParsedType

  def amfNode: AmfObject

  def localType: Option[ITypeDefinition]

  def definition: ITypeDefinition

  //def allowsQuestion: Boolean

  def attribute(n: String): Option[IAttribute]

  def attributeValue(n: String): Option[Any]

  def attributes: Seq[IAttribute]

  def attributes(n: String): Seq[IAttribute]

  def elements: Seq[IHighLevelNode]

  def element(n: String): Option[IHighLevelNode]

  def elements(n: String): Seq[IHighLevelNode]

  // def value: Any

  //def propertiesAllowedToUse(): Array[IProperty]
  //    def add(node: IParseResult): Unit
  //
  //    def remove(node: IParseResult): Unit
  //
  //    def dump(flavor: String): String

  //def findElementAtOffset(offset: Int): IHighLevelNode
  //def findReferences(): Array[IParseResult]
  //def copy(): IHighLevelNode
  //def resetChildren(): Unit
  //def findById(id: String): Nothing
  def associatedType: ITypeDefinition

  //def optionalProperties(): Array[String]
  //def createIssue(error: IStatus): ValidationIssue
  //def getMaster(): IParseResult
  //def isAuxilary(): Boolean
  //def reusedNode(): IHighLevelNode
  //def getMasterCounterPart(): IHighLevelNode
  //def getSlaveCounterPart(): IHighLevelNode
  //def getLastSlaveCounterPart(): IHighLevelNode
  //def toJSON(options: SerializeOptions): Any

  override protected def selectNodeWhichContainsPosition(pos: Int): Option[IParseResult] = {
    if (sourceInfo.containsPosition(pos)) {
      val path = astUnit.path
      val matchedSiblings = children
        .filter(x => {
          val up = x.unitPath
          up.isEmpty || up.contains(path)
        })
        .flatMap(_.getNodeByPosition(pos))
      if (matchedSiblings.nonEmpty)
        matchedSiblings.headOption
      else
        Some(this)
    } else {
      None
    }
  }

  override protected def selectNodeWhichContainsPosition(position: Position): Option[IParseResult] = {
    if (sourceInfo.containsPosition(position)) {
      val path = astUnit.path
      val matchedSiblings = children
        .filter(x => {
          val up = x.unitPath
          up.isEmpty || up.contains(path)
        })
        .flatMap(_.getNodeByPosition(position))
      if (matchedSiblings.nonEmpty) {
        matchedSiblings.headOption
      } else {
        Some(this)
      }
    } else {
      None
    }
  }

  def newChild(prop: IProperty, typeHint: Option[ITypeDefinition] = None): Option[IParseResult]
}
