package org.mulesoft.high.level.implementation

import amf.core.annotations.SourceAST
import amf.core.metamodel.Field
import amf.core.model.document.BaseUnit
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition}
import amf.core.model.domain._
import amf.core.parser.{Annotations, Value}
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IProject, ISourceInfo}
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, NodeRange}
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model.YPart


class ASTPropImpl(
        _node: AmfObject,
        _unit:BaseUnit,
        _parent: Option[IHighLevelNode],
        _def: ITypeDefinition,
        _prop: Option[IProperty],
        buffer: IValueBuffer) extends BasicASTNode(_node, _unit, _parent) with IAttribute {


    def definition: Option[ITypeDefinition] = Option(_def)


    override def asAttr: Option[IAttribute] = Some(this)

    def annotations: Seq[IAttribute] = Array[IAttribute]()

    override def property: Option[IProperty] = _prop

    def value:Option[Any] = buffer.getValue

    def setValue(newValue: Any): Unit = buffer.setValue(newValue)

    def name:String = _prop.flatMap(_.nameId).getOrElse("")

    override def printDetails(indent: String = ""): String = {
        var className = if (definition.isDefined) definition.get.nameId.getOrElse("") else ""
        var definitionClassName:String = printDefinitionClassName
        var result = s"$indent$name: $className[$definitionClassName]  =  ${value.getOrElse("$None")}"
        var ranges = sourceInfo.ranges
        if(ranges.lengthCompare(1)==0){
            result += s"; #range: ${ranges.head}"
        }
        else{
            result += (s"\n$indent #ranges:" + ranges.map(r=>s"\n$indent  "+r).mkString(""))
        }
        result += "\n"
        result
    }

    override def isAttr: Boolean = true

    override def isUnknown: Boolean = false

    override def sourceInfo:SourceInfo = buffer._sourceInfo

    override def initSources(referingUnit:Option[ASTUnit],externalPath:Option[String]):Unit = {
        buffer.initSourceInfo(astUnit.project,referingUnit,externalPath)
    }
}

object ASTPropImpl{

    def apply(
        _node: AmfObject,
        _unit: BaseUnit,
        _parent: Option[IHighLevelNode],
        _def: ITypeDefinition,
        _prop: Option[IProperty],
        buffer: IValueBuffer): ASTPropImpl = new ASTPropImpl(_node,_unit,_parent,_def,_prop,buffer)
}

trait IValueBuffer {

    protected var rangesOpt:Option[Seq[NodeRange]] = None

    var _sourceInfo:SourceInfo = SourceInfo()
    _sourceInfo.withSources(yamlNodes)

    def getValue: Option[Any]

    def setValue(value: Any): Unit

    def yamlNodes: Seq[YPart]

    def sourceInfo:ISourceInfo = _sourceInfo

    def initSourceInfo(project:IProject,referingUnit:Option[ASTUnit],externalPath:Option[String]):Unit
            = _sourceInfo.init(project,referingUnit,externalPath)
}

class BasicValueBuffer(domainElement:AmfObject, field:Field, index:Int = -1) extends IValueBuffer {

    override def getValue: Option[Any] = domainElement.fields.get(field) match {
        case scalar:AmfScalar => Some(scalar.value)
        case array:AmfArray =>
            if(index>=0 && array.values.lengthCompare(index)>0){
                array.values(index) match {
                    case scalar: AmfScalar => Some(scalar.value)
                    case obj: AmfObject => Some(obj)
                    case _ => None
                }
            }
            else {
                None
            }
        case obj: AmfObject => Some(obj)
        case _ => None
    }

    override def setValue(value: Any): Unit = {
        domainElement.fields.get(field) match {
//            case array: AmfArray =>
//                if(index>=0 && array.values.lengthCompare(index)>0) {
//                    toAmfElement(value).map(e => {
//                        var newArrayValue = ListBuffer[AmfElement]() ++= array.values
//                        newArrayValue(index) = e
//                        domainElement.set(field,AmfArray(newArrayValue))
//                    })
//                }
            case _ =>
                var annotations = getValueNode.map(_.annotations).getOrElse(Annotations())
                toAmfElement(value,annotations).map(domainElement.set(field, _))
        }
    }

    private def getValueNode:Option[AmfElement] = {
        Option(domainElement.fields.get(field))
    }

    private def getAMFValue:Option[Value] = {
        Option(domainElement.fields.getValue(field))
    }

    protected def toAmfElement(_value: Any, annotations:Annotations): Option[AmfElement] = _value match {
            case value: String => Some(AmfScalar(value,annotations))
            case value: Boolean => Some(AmfScalar(value,annotations))
            case value: Int => Some(AmfScalar(value,annotations))
            case value: Float => Some(AmfScalar(value,annotations))
            case value: AmfElement => Some(value)
            case _ => None
    }

    def yamlNodes: Seq[YPart] = getAMFValue.flatMap(amfValue => amfValue.value match {

        case arr: AmfArray =>
            val valSeq = arr.values
            if (index >= 0 && valSeq.lengthCompare(index) > 0) {
                valSeq(index).annotations.find(classOf[SourceAST])
            }
            else {
                None
            }
        case _ => amfValue.annotations.find(classOf[SourceAST])
    }).map(_.ast) match {
        case Some(n) => List(n)
        case _ => Seq()
    }
}

object BasicValueBuffer {
    def apply(de:AmfObject, f:Field, i:Int = -1) = new BasicValueBuffer(de,f,i)
    def apply(de:AmfObject, f:Field) = new BasicValueBuffer(de,f)
}

class CompositeValueBuffer(buffers:Seq[IValueBuffer]) extends IValueBuffer {

    override def getValue: Option[Any] = {
        if(buffers.isEmpty){
            None
        }
        else{
            buffers.head.getValue
        }
    }

    override def setValue(value: Any): Unit = {
        buffers.foreach(_.setValue(value))
    }

    override def initSourceInfo(project:IProject,referingUnit:Option[ASTUnit],externalPath:Option[String]): Unit = {
        _sourceInfo.withSources(buffers.flatten(_.yamlNodes).distinct)
        _sourceInfo.init(project,referingUnit,externalPath)
        buffers.foreach(_.initSourceInfo(project,referingUnit,externalPath))
    }

    override def yamlNodes: Seq[YPart] = buffers.flatMap(_.yamlNodes)
}

object CompositeValueBuffer {
    def apply(buffers:Seq[IValueBuffer]) = new CompositeValueBuffer(buffers)
}


class JSONValueBuffer(element:AmfElement,hlNode:IHighLevelNode, json:Option[JSONWrapper]) extends IValueBuffer {

    json match {
        case Some(n) => n match {
            case yjw:YJSONWrapper => _sourceInfo.withSources(List(yjw.source))
            case _ =>
        }
        case _ =>
    }

    override def getValue: Option[Any] = json match {
        case Some(j) => j.kind match {
            case STRING => j.value(STRING)
            case NUMBER => j.value(NUMBER)
            case BOOLEAN => j.value(BOOLEAN)
            case _ => json
        }

        case _ => json
    }

    override def setValue(value: Any): Unit = {}

    override def yamlNodes: Seq[YPart] = json match {
        case Some(n) => n match {
            case yw:YJSONWrapper => List(yw.yamlNode)
            case _ => Seq()
        }
        case _ => Seq()
    }

    override def initSourceInfo(project:IProject,referingUnit:Option[ASTUnit],externalPath:Option[String]): Unit = {
        referingUnit.foreach(u=>{
            var pm = u.positionsMapper
            json.foreach(x=>pm.initRange(x.range))
        })
        _sourceInfo.init(project,referingUnit,externalPath)
    }

}

object JSONValueBuffer{
    def apply(element:AmfElement,hlNode:IHighLevelNode, json:Option[JSONWrapper]):JSONValueBuffer = new JSONValueBuffer(element,hlNode,json)

    def apply(element:AmfElement,hlNode:IHighLevelNode):JSONValueBuffer = {
        var json = element.annotations.find(classOf[SourceAST]).flatMap(yn => YJSONWrapper(yn.ast))
        JSONValueBuffer(element,hlNode,json)
    }
}