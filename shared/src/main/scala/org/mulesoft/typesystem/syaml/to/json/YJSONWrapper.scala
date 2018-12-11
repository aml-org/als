package org.mulesoft.typesystem.syaml.to.json

import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.json.interfaces.{JSONWrapper, JSONWrapperKind}
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.yaml.model._

import scala.collection.mutable.ListBuffer

class YJSONWrapper(nodeValue:YValue,nodeOpt:Option[YNode],val key:Option[String]) extends org.mulesoft.typesystem.json.interfaces.JSONWrapper {

    def this(node:YNode) = {
        this(node.value,Some(node),None)
        _YNodeOpt = Some(node)
    }

    def this(nodeValue:YValue) = this(nodeValue,None,None)

    def this(mapEntry:YMapEntry) = {
        this(mapEntry.value.value,Some(mapEntry.value),Some(mapEntry.key.value.toString))
        _YMapEntryOpt = Some(mapEntry)
    }

    var _YNodeOpt:Option[YNode] = None

    var _YMapEntryOpt:Option[YMapEntry] = None

    private var _value:Any = _

    private var _kind:JSONWrapperKind[_] = NULL

    private var _properties:Option[scala.collection.mutable.Map[String,YJSONProperty]] = None

    var _range:YRange = YRange.empty

    def source:YPart = _YMapEntryOpt match {
        case Some(x) => x
        case None => _YNodeOpt match {
            case Some(y) => y
            case None => nodeValue
        }
    }

    override def propertyValue(name: String): Option[YJSONWrapper] = getProperty(name) match {
        case Some(p) => Some(p.value)
        case _ => None
    }

    override def kind:JSONWrapperKind[_] = _kind

    override def value:Any = _value

    override def range:YRange = _range

    def yamlNode:YValue = nodeValue

    override protected def getProperty(name:String):Option[YJSONProperty] = {
        _properties match {
            case Some(map) => map.get(name)
            case None => None
        }
    }

    override def properties:Seq[YJSONProperty] = {
        _properties match {
            case None => scala.collection.immutable.List()
            case Some(map) => ListBuffer[YJSONProperty]() ++= map.values
        }
    }

    def initPositions(pm:IPositionsMapper):Unit = {
        var yPart:YPart = nodeOpt match {
            case Some(n) => n
            case _ => nodeValue
        }
        _range = YRange(yPart,Option(pm))
        pm.initRange(range)
        kind match {
            case OBJECT => _properties.foreach(_.values.foreach(_.value.initPositions(pm)))
            case ARRAY => value(ARRAY).get.foreach({
                case w: YJSONWrapper => w.initPositions(pm)
                case _ =>
            })
            case _ =>
        }

    }

    private def init:Any = {

        var yPart:YPart = nodeOpt match {
            case Some(n) => n
            case _ => nodeValue
        }

        var actualValue:Any = null
        nodeValue match {
            case yMap:YMap =>
                actualValue = this
                _properties = Some(scala.collection.mutable.Map[String,YJSONProperty]())
                yMap.entries.foreach(e=>{
                    e.key.value match {
                        case yScalar:YScalar =>
                            var sValue = yScalar.value
                            var name = sValue match {
                                case name:String => name
                                case num:Number => "" + num
                                case bool:Boolean => "" + bool
                                case _ => throw new Error("Not a string used as JOSN object key: " + sValue)
                            }
                            var propValue = YJSONWrapper(e)
                            val prop = YJSONProperty(name, propValue)
                            _properties.get.put(name,prop)

                        case _ => throw new Error("Nonscalar YAML node as JSON key: " + nodeValue)
                    }
                })
            case ySeq:YSequence => actualValue = ySeq.nodes.map(YJSONWrapper(_))
            case yScalar:YScalar => actualValue = yScalar.value
            case _ => throw new Error("Unexpected YAML value: " + nodeValue)
        }
        _value = actualValue
        _kind = JSONWrapperKind.wrapperKind(actualValue)
    }

    init
}

object YJSONWrapper {

    def apply(node:YNode):YJSONWrapper = new YJSONWrapper(node)

    def apply(nodeValue:YValue):YJSONWrapper = new YJSONWrapper(nodeValue)

    def apply(mapEntry:YMapEntry):YJSONWrapper = new YJSONWrapper(mapEntry)

    def apply(yPart:YPart):Option[YJSONWrapper] = yPart match {
        case n: YNode => Some(YJSONWrapper(n))
        case v: YValue => Some(YJSONWrapper(v))
        case me: YMapEntry => Some(YJSONWrapper(me))
        case _ => None
    }
}
