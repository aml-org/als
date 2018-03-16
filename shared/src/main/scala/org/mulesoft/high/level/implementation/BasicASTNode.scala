package org.mulesoft.high.level.implementation

import amf.core.annotations.SourceAST
import amf.core.model.document.BaseUnit
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IParseResult}
import amf.core.model.domain.AmfObject

class BasicASTNode(
        _node: AmfObject,
        _baseUnit: BaseUnit,
        var _parent: Option[IHighLevelNode]) extends IParseResult {

    private var _astUnit: Option[ASTUnit] = None

    private var _sourceInfo:SourceInfo = SourceInfo()
    _node.annotations.find(classOf[SourceAST]).map(_.ast) match {
        case Some(yPart) => _sourceInfo.withSources(List(yPart))
        case None =>
    }

    def asAttr: Option[IAttribute] = None

    def asElement: Option[ASTNodeImpl] = None

    def root: Option[IHighLevelNode] = {
        parent match {
            case Some(p) => p.root
            case None => None
        }
    }

    var knownProperty: IProperty = _
    var needSequence: Boolean = _
    var needMap: Boolean = _
    var invalidSequence: Boolean = _

    def printDetails(indent: String): String = s"${indent}Unkown\n"

    def testSerialize(indent: String): String = s"${indent}Unkown\n"

    def amfNode: AmfObject = _node

    def amfBaseUnit: BaseUnit = _baseUnit

    def parent: Option[IHighLevelNode] = _parent

    def setParent(parent: IHighLevelNode):Unit = _parent = Option(parent)

    def isElement = false

    def children: Seq[IParseResult] = Array[IParseResult]()

    def isAttr: Boolean = false

    def isUnknown: Boolean = true

    def property: Option[IProperty] = None

    protected def printDefinitionClassName:String = {
        (for {
            prop <- property
            range <- prop.range
            n <- range.nameId
        } yield {
            var result:String = range.nameId.getOrElse("")
            if(range.isArray){
                for {
                    ct <- range.array.get.componentType
                    ctName <- ct.nameId
                }
                yield {
                    result = result + s"(array[$ctName])"
                }
            }
            result
        }).getOrElse("")
    }

    def astUnit: ASTUnit = _astUnit.orNull

    def setASTUnit(u:ASTUnit):Unit = _astUnit = Option(u)

    def initSources(referingUnit:Option[ASTUnit],externalPath:Option[String]):Unit = {
        _sourceInfo.init(astUnit.project,referingUnit,externalPath)
    }

    def sourceInfo:SourceInfo = _sourceInfo
}
