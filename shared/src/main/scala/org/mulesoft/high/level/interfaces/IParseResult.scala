package org.mulesoft.high.level.interfaces

import amf.core.annotations.{SourceAST, SourceNode}
import amf.core.model.document.BaseUnit
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import amf.core.model.domain.AmfObject
import org.mulesoft.typesystem.json.interfaces.NodeRange
import org.yaml.model.YPart

trait IParseResult {

    def amfNode: AmfObject
    //def name: String
    //def optional: Boolean

    def amfBaseUnit: BaseUnit

    def root: Option[IHighLevelNode]

    //def isSameNode(n: IParseResult): Boolean

    def parent: Option[IHighLevelNode]

    def setParent(node: IHighLevelNode): Unit

    def children: Seq[IParseResult]

    //def directChildren: Array[IParseResult]
    //def isAttached: Boolean
    //def isImplicit: Boolean
    def isAttr: Boolean

    def asAttr: Option[IAttribute]

    def isElement: Boolean

    def asElement: Option[IHighLevelNode]

    //def localId(): String
    //def fullLocalId(): String
    def isUnknown: Boolean

    def property: Option[IProperty]

    //def id: String
    //def computedValue(name: String): Any

    //def validate(acceptor: ValidationAcceptor): Unit
    def printDetails(indent: String=""): String

    def printDetails: String = printDetails()

    def astUnit: IASTUnit

    //def getKind: NodeKind
    //def getLowLevelStart(): Nothing
    //def getLowLevelEnd(): Nothing
    //def version(): Nothing
    //def setJSON(`val`: Any): Nothing
    //def getJSON(): Any

    def sourceInfo:ISourceInfo

    def getNodeByPosition(pos:Int):Option[IParseResult] =
        selectNodeWhichContainsPosition(pos).map(n=>{
            var posOffset = astUnit.positionsMapper.offset(pos)
            var result = n
            if(result.sourceInfo.isYAML) {
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

    protected def selectNodeWhichContainsPosition(pos:Int):Option[IParseResult] =
        if (sourceInfo.containsPosition(pos)) Some(this)
        else None

    def unitPath:Option[String] = {
        var opt:Option[String] = amfNode.annotations.find(classOf[SourceNode]).map(_.node.sourceName).orElse(Option(amfNode.id))
        opt match {
            case Some(str) =>
                var result = str
                var ind = str.indexOf("#")
                if(ind>=0){
                    result = str.substring(0,ind)
                }
                Some(result)
            case _ => None
        }

    }
}
