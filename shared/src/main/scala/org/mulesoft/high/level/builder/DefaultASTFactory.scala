package org.mulesoft.high.level.builder

import amf.core.annotations.SourceAST
import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.{CustomDomainPropertyModel, PropertyShapeModel}
import amf.core.model.document.BaseUnit
import amf.core.model.domain._
import amf.core.metamodel.{Field, Obj}
import amf.plugins.domain.webapi.metamodel.{ParameterModel, PayloadModel}
import org.mulesoft.high.level.implementation._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition}
import org.mulesoft.typesystem.project.ITypeCollectionBundle
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model.YPart

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

abstract class DefaultASTFactory extends IASTFactory {

    val matchers:mutable.Map[String,mutable.Map[String,IPropertyMatcher]]
            = mutable.Map[String,mutable.Map[String,IPropertyMatcher]]()

    def registerPropertyMatcher(typeName:String,propertyName:String,matcher:IPropertyMatcher):Unit = {
        var mapOpt:Option[mutable.Map[String,IPropertyMatcher]] = matchers.get(typeName)
        mapOpt match {
            case Some(m) =>
            case None =>
                val m = mutable.Map[String, IPropertyMatcher]()
                matchers.put(typeName,m)
                mapOpt = Some(m)
        }
        mapOpt.get.put(propertyName,matcher)
    }

    def registerPropertyMatcher(typeName:String,propertyName:String,field:Field):Unit = {
        registerPropertyMatcher(typeName,propertyName,FieldMatcher(field))
    }

    def getMatcherByTypeName(typeName:String, propertyName:String):Option[IPropertyMatcher]
        = matchers.get(typeName).flatMap(_.get(propertyName))

    def getMatcher(t:ITypeDefinition,propertyName:String):Option[IPropertyMatcher] = {

        t.nameId.flatMap(getMatcherByTypeName(_,propertyName)) match {
            case Some(matcher) => Some(matcher)
            case None =>
                var gotMatcher = false
                var result:Option[IPropertyMatcher] = None
                t.allSuperTypes.foreach(st => {
                    if(!gotMatcher){
                        st.nameId.flatMap(getMatcherByTypeName(_,propertyName)) match {
                            case Some(matcher) =>
                                result = Some(matcher)
                                gotMatcher = true
                            case None =>
                        }
                    }
                })
                result
        }
    }

    override def getPropertyValues(node: IHighLevelNode, prop: IProperty, bundle:ITypeCollectionBundle):Seq[BasicASTNode]
        = getPropertyValues(node,node.amfNode,node.amfBaseUnit,prop,bundle)

    override def getPropertyValues(node:IHighLevelNode, amfNode:AmfObject, baseUnit:BaseUnit, prop:IProperty, bundle:ITypeCollectionBundle): Seq[BasicASTNode] = {

        var resultOpt = for {
            propName <- prop.nameId
            matcher <- getMatcher(node.definition,propName)
        }
        yield {
            var propertyMatches = matcher.operate(amfNode,node)
            var range:ITypeDefinition = prop.range.get
            for{
                array <- range.array
                ct <- array.componentType
            } yield{
                range = ct
            }

            propertyMatches.map({
                case am: AttributeMatchResult => ASTPropImpl (
                    am.node, baseUnit, Option (node), discriminate(range,am.node,None), Option(prop), am.buffer)
                case em: ElementMatchResult =>
                    val nominalType = determineUserType(em.node, Option(prop), Option(node), bundle)
                    var hlNode = ASTNodeImpl(
                    em.node, baseUnit, Option(node), discriminate(range,em.node,nominalType), Option(prop))
                    em.yamlNode.foreach(x=>hlNode.sourceInfo.withSources(List(x)))
                    nominalType.foreach(hlNode.setLocalType)
                    hlNode
            })
        }
        resultOpt.getOrElse(Seq())
    }


    def extractShape(obj: AmfObject): Option[Shape] = {

        var dElementOpt:Option[DomainElement] = obj match {
            case bu:BaseUnit => Option(bu.fields.getValue(DocumentModel.Encodes)).map(_.value).map({
                case de:DomainElement => de
                case _ => null
            })
            case de:DomainElement => Some(de)
            case _ => None
        }

        if(dElementOpt.isEmpty){
            None
        }
        else {
            var dElement = dElementOpt.get
            var shapeOpt: Option[Shape] = dElement.meta match {
                case PropertyShapeModel => Option(dElement.fields.get(PropertyShapeModel.Range)
                    .asInstanceOf[Shape])
                case CustomDomainPropertyModel => Option(dElement.fields.get(CustomDomainPropertyModel.Schema).asInstanceOf[Shape])
                case PayloadModel => Option(dElement.fields.get(PayloadModel.Schema).asInstanceOf[Shape])
                case ParameterModel => Option(dElement.fields.get(ParameterModel.Schema).asInstanceOf[Shape])
                case shapeModel: ShapeModel => Some(dElement.asInstanceOf[Shape])
                case _ => None
            }
            shapeOpt
        }
    }
}

sealed abstract class MatchResult(val node:AmfObject)

class AttributeMatchResult(node:AmfObject, val buffer:IValueBuffer) extends MatchResult(node) {}

object AttributeMatchResult{
    def apply(node:AmfObject, buffer:IValueBuffer):AttributeMatchResult = new AttributeMatchResult(node,buffer)
}

class ElementMatchResult(node:AmfObject,val yamlNode:Option[YPart]) extends MatchResult(node) {}

object ElementMatchResult{
    def apply(node:AmfObject):ElementMatchResult
            = new ElementMatchResult(node,node.annotations.find(classOf[SourceAST]).map(_.ast))

    def apply(node:AmfObject,yamlNodeOpt:Option[YPart]):ElementMatchResult
            = new ElementMatchResult(node,yamlNodeOpt)
}

trait IPropertyMatcher {

    var bufferConstructor:Option[(AmfObject,IHighLevelNode)=>IValueBuffer] = None

    var yamlPath:Option[Seq[String]] = None

    def withCustomBuffer(bufConstructor:(AmfObject,IHighLevelNode)=>IValueBuffer):IPropertyMatcher = {
        bufferConstructor = Option(bufConstructor)
        this
    }

    def withYamlPath(yp:Seq[String]):IPropertyMatcher = {
        yamlPath = Option(yp)
        this
    }

    def withYamlPath(yp:String):IPropertyMatcher = withYamlPath(List(yp))

    var STRICT:Boolean = true

    def operate(obj:AmfObject, hlNode:IHighLevelNode):Seq[MatchResult] = {
        var result = doOperate(obj,hlNode)
        bufferConstructor match {
            case Some(c) => result = result.map(mr=>AttributeMatchResult(mr.node,c(mr.node,hlNode)))
            case None =>
        }
        yamlPath match {
            case Some(yp) =>
                if(yp.lengthCompare(1)==0) {
                    var key = yp.head
                    result = result.flatMap({
                        case er: ElementMatchResult =>
                            var srcOpt = er.yamlNode.flatMap(YJSONWrapper(_))
                                .flatMap(_.propertyValue(key)).map(_.source)
                            srcOpt match {
                                case Some(yn) => Some(ElementMatchResult(er.node,srcOpt))
                                case None => None
                            }

                        case ar: AttributeMatchResult => Some(ar)
                    })
                }
            case None =>
        }
        result
    }

    def doOperate(obj:AmfObject, hlNode:IHighLevelNode):Seq[MatchResult]

    def + (that:IPropertyMatcher):IPropertyMatcher = MatchersChain(this,that)
    def + (field:Field):IPropertyMatcher = this + FieldMatcher(field)
    def * (that:IPropertyMatcher):IPropertyMatcher = CompositeMatcher(this,that)
    def * (field:Field):IPropertyMatcher = this * FieldMatcher(field)
    def ifType(model:Obj):IPropertyMatcher = MatchersChain(this,TypeFilter(model,STRICT))
    def ifSubtype(model:Obj):IPropertyMatcher = MatchersChain(this,TypeFilter(model))
    def & (that:IPropertyMatcher):IPropertyMatcher = AndMatcher(List(this,that))
    def & (field:Field):IPropertyMatcher = AndMatcher(List(this,FieldMatcher(field)))
    def | (that:IPropertyMatcher):IPropertyMatcher = OrMatcher(List(this,that))
    def | (field:Field):IPropertyMatcher = OrMatcher(List(this,FieldMatcher(field)))
}

class ThisMatcher extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = List(ElementMatchResult(obj))

    override def + (that:IPropertyMatcher):IPropertyMatcher = that.withCustomBuffer(this.bufferConstructor.orNull)
}

object ThisMatcher {
    def apply():ThisMatcher = new ThisMatcher()
}

class BaseUnitMatcher extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode:IHighLevelNode): Seq[MatchResult] = List(ElementMatchResult(hlNode.amfBaseUnit))
}

object BaseUnitMatcher {
    def apply():BaseUnitMatcher = new BaseUnitMatcher()
}

class ParentMatcher extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] =
        hlNode.parent match {
            case Some(p) => List(ElementMatchResult(p.amfNode))
            case None => Seq()
        }
}

object ParentMatcher {
    def apply():ParentMatcher = new ParentMatcher()
}

class TypeFilter(_clazz:Obj,strict:Boolean) extends IPropertyMatcher {

    val thisIds:Seq[String] = _clazz.`type`.map(_.name)

    override def doOperate(obj: AmfObject, hlNode:IHighLevelNode): Seq[MatchResult] = {
        obj match {
            case de:DomainElement =>
                var thatIds:List[String] = obj.asInstanceOf[DomainElement].meta.`type`.map(_.name)
                if((!strict||thisIds.lengthCompare(thatIds.length)==0)
                    &&thisIds.forall(thatIds.contains(_))){
                    List(ElementMatchResult(obj))
                }
                else Seq()
            case _ => Seq()
        }
    }
}

object TypeFilter {
    def apply(t:Obj):TypeFilter = new TypeFilter(t,false)
    def apply(t:Obj,strict:Boolean):TypeFilter = new TypeFilter(t,strict)
}

/**
  * Only applicable to scalars
  */
class AndMatcher(matchers:Seq[IPropertyMatcher]) extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode:IHighLevelNode): Seq[MatchResult] = {

        if(matchers.isEmpty){
            Seq()
        }
        else{
            var table:Seq[ListBuffer[MatchResult]]
                = matchers.map(ListBuffer[MatchResult]()++=_.operate(obj,hlNode))
            var size = table.foldLeft(0){(x,i) => math.max(x,i.length)}
            var result: ListBuffer[MatchResult] = ListBuffer()
            for(i<-0 to size){
                var arr:ListBuffer[AttributeMatchResult] = ListBuffer()
                table.filter(_.lengthCompare(i)>0).foreach(_(i) match {
                    case amr : AttributeMatchResult => arr += amr
                    case _ =>
                })
                arr.length match {
                    case 0 =>
                    case 1 => result += arr.head
                    case _ => result += AttributeMatchResult(arr.head.node,CompositeValueBuffer(arr.map(_.buffer)))
                }
            }
            result
        }
    }
}

object AndMatcher {
    def apply(matchers:Seq[IPropertyMatcher]):AndMatcher = new AndMatcher(matchers)
}

class OrMatcher(matchers:Seq[IPropertyMatcher]) extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode:IHighLevelNode): Seq[MatchResult] = {

        var result:Seq[MatchResult] = Seq()
        matchers.find(x=>{
            var matches = x.operate(obj,hlNode)
            if(matches.isEmpty){
                false
            }
            else {
                result = matches
                true
            }
        })
        result
    }
}

object OrMatcher {
    def apply(matchers:Seq[IPropertyMatcher]):OrMatcher = new OrMatcher(matchers)
}


class MatchersChain(left: IPropertyMatcher, right: IPropertyMatcher) extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode:IHighLevelNode): Seq[MatchResult] = {
        var result: ListBuffer[MatchResult] = ListBuffer()
        left.operate(obj,hlNode).foreach({
            case em: ElementMatchResult => result ++= right.operate(em.node,hlNode)
            case _ =>
        })
        result
    }
}

object MatchersChain {
    def apply(left: IPropertyMatcher, right: IPropertyMatcher):MatchersChain = new MatchersChain(left,right)
}

class CompositeMatcher(first: IPropertyMatcher, second: IPropertyMatcher) extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode:IHighLevelNode): Seq[MatchResult] = {
        var result:ListBuffer[MatchResult] = ListBuffer()
        result ++= first.operate(obj,hlNode)
        result ++= second.operate(obj,hlNode)
        result
    }
}

object CompositeMatcher {
    def apply(left: IPropertyMatcher, right: IPropertyMatcher):CompositeMatcher = new CompositeMatcher(left,right)
}

class FieldMatcher(field: Field) extends IPropertyMatcher {

    override def doOperate(obj: AmfObject, hlNode: IHighLevelNode): Seq[MatchResult] = {
        var result: ListBuffer[MatchResult] = ListBuffer()
        obj.fields.get(field) match {
            case dataNode:DataNode =>
                dataNode.annotations.find(classOf[SourceAST]) match {
                    case Some(yn) => result += AttributeMatchResult(
                        obj, JSONValueBuffer(obj,hlNode,YJSONWrapper(yn.ast)))
                    case _ =>
                }

            case amfObj: AmfObject => result += ElementMatchResult(amfObj)
            case scalar: AmfScalar => result += AttributeMatchResult(obj, bufferConstructor match {
                    case Some(c) => c.apply(obj,hlNode)
                    case _ => BasicValueBuffer(obj, field)
                })
            case array: AmfArray =>
                var i = 0
                array.values.foreach(x => {
                    x match {
                        case amfObj: AmfObject => result += ElementMatchResult(amfObj)
                        case scalar: AmfScalar => result += AttributeMatchResult(obj, BasicValueBuffer(obj, field, i))
                        case _ =>
                    }
                    i += 1
                })
            case _ =>
        }
        result
    }
}

object FieldMatcher {
    def apply(field: Field) = new FieldMatcher(field)
}
