package org.mulesoft.high.level.implementation

import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.document.BaseUnit
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition, IUniverse}
import amf.core.model.domain.{AmfObject, AmfScalar, DomainElement}
import org.mulesoft.high.level.builder.{ASTFactoryRegistry, NodeBuilder}
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IParseResult}
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.positioning.IPositionsMapper
import org.yaml.model.YPart

import scala.collection.GenTraversableOnce
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class ASTNodeImpl(
        _node: AmfObject,
        _unit: BaseUnit,
        parent: Option[IHighLevelNode],
        var _def: ITypeDefinition,
        _prop: Option[IProperty]) extends BasicASTNode(_node, _unit, parent) with IHighLevelNode {


    var _ptype: ITypeDefinition = _

    var _children: ListBuffer[BasicASTNode] = ListBuffer()

    var _associatedDef: ITypeDefinition = _

    var _localType:Option[ITypeDefinition] = None

    override def amfNode:AmfObject = _node

    def parsedType(): ITypeDefinition = _ptype

    def localType: Option[ITypeDefinition] = _localType

    def setLocalType(lt:ITypeDefinition):Unit = _localType = Option(lt)

    override def asElement: Option[ASTNodeImpl] = Some(this)

    def setAssociatedType(d: ITypeDefinition):Unit = _associatedDef = d

    def associatedType: ITypeDefinition = _associatedDef


    override def printDetails(indent: String=""): String = NodePrinter.printElement(this,indent)

    def name: String = ""

    override def isElement: Boolean = true

    var _universe: IUniverse = _

    def universe(): IUniverse = _universe

    def setUniverse(u: IUniverse):Unit = _universe = u

    def patchType(d: ITypeDefinition):Unit = {
        _def = d
        _associatedDef = null
        _children = null
    }

    override def children: Seq[IParseResult] = _children

    def setChildren(arr: Seq[BasicASTNode]):Unit = _children = ListBuffer[BasicASTNode]() ++= arr

    def addChild(ch: BasicASTNode):Unit = _children += ch

    def attribute(n: String): Option[IAttribute] = attributes(n).headOption

    def attributeValue(n: String): Option[Any] = attribute(n).map(_.value)

    def attributes(n: String): Seq[IAttribute] = attributes.filter(_.name == n)

    def attributes: Seq[IAttribute] = children.flatMap(_.asAttr)

    def elements: Seq[IHighLevelNode] = children.flatMap(_.asElement)

    def element(n: String): Option[IHighLevelNode] = elements(n).headOption

    def elements(n: String): Seq[IHighLevelNode] = elements.flatMap(x=>x.property match {
        case Some(p) => p.nameId match {
            case Some(n1) => if(n==n1) Some(x) else None
            case _ => None
        }
        case None => None
    })

    def definition: ITypeDefinition = _def

    override def property: Option[IProperty] = _prop

    override def setASTUnit(u:ASTUnit):Unit = {
        super.setASTUnit(u)
        _children.foreach(_.setASTUnit(u))
    }

    override def initSources(referingUnit:Option[ASTUnit],externalPath:Option[String]):Unit = {
        super.initSources(referingUnit,externalPath)
        var isExternal = sourceInfo.externalLocationPath.isDefined || sourceInfo.includePathLabel.isDefined
        if(isExternal){
            initChildrenSources(None,sourceInfo.externalLocationPath)
        }
        else {
            initChildrenSources(referingUnit,externalPath)
        }
    }

    protected def initChildrenSources(_referingUnit:Option[ASTUnit],externalPath:Option[String]):Unit
            = {
        var referingUnit:Option[ASTUnit] = _referingUnit
        Option(this.amfNode.fields.get(LinkableElementModel.Target)).foreach({
            case de: DomainElement => Option(de.fields.get(LinkableElementModel.Label)) match {
                case Some(link) => link match {
                    case sc:AmfScalar =>
                        val path = TypeBuilder.unitPath(sc.value.toString)
                        if(!referingUnit.map(_.path).contains(path)){
                            referingUnit = astUnit.project.units.get(path)
                        }
                    case _ =>
                }
                case _ =>
            }
            case _ =>
        })
        _children.foreach(_.initSources(referingUnit,externalPath))
    }

    def newChild(prop:IProperty,typeHint:Option[ITypeDefinition]=None):Option[IParseResult] = {

        var format = astUnit.project.language
        ASTFactoryRegistry.getFactory(format) match {
            case Some(factory) =>
                factory.newChild(this,prop,typeHint).flatMap({
                    case bn:BasicASTNode =>
                        addChild(bn)
                        bn.setASTUnit(astUnit)
                        //bn.asElement.foreach(NodeBuilder.fillChildren(_,factory,astUnit.project.types))
                        Some(bn)
                    case _ => None
                })
            case _ => None
        }
    }

}

object ASTNodeImpl {
    def apply(
         _node: AmfObject,
         unit:BaseUnit,
         parent: Option[IHighLevelNode],
         _def: ITypeDefinition,
         _prop: Option[IProperty])
        = new ASTNodeImpl(_node,unit,parent,_def,_prop)
}