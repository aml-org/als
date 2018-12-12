package org.mulesoft.positioning

import org.mulesoft.typesystem.json.interfaces.NodeRange
import org.mulesoft.typesystem.syaml.to.json.YRange
import org.yaml.model._

class YamlLocation private {

    private var _node: Option[YamlPartWithRange[YNode]] = None

    private var _mapEntry: Option[YamlPartWithRange[YMapEntry]] = None

    private var _value: Option[YamlPartWithRange[YValue]] = None

    private var _keyNode: Option[YamlPartWithRange[YNode]] = None

    private var _keyValue: Option[YamlPartWithRange[YValue]] = None

    private var _parentStack: List[YamlLocation] = List()

    var _isEmpty:Boolean = true

    def node:Option[YamlPartWithRange[YNode]] = _node

    def mapEntry:Option[YamlPartWithRange[YMapEntry]] = _mapEntry

    def value:Option[YamlPartWithRange[YValue]] = _value

    def keyNode:Option[YamlPartWithRange[YNode]] = _keyNode

    def keyValue:Option[YamlPartWithRange[YValue]] = _keyValue

    def parentStack: List[YamlLocation] = _parentStack

    def isEmpty:Boolean = _isEmpty

    def nonEmpty:Boolean = !_isEmpty

    def withNode(n:YamlPartWithRange[YNode]):YamlLocation = {
        _node = Option(n)
        _isEmpty = false
        this
    }

    def withMapEntry(n:YamlPartWithRange[YMapEntry]):YamlLocation = {
        _mapEntry = Option(n)
        _isEmpty = false
        this
    }

    def withValue(n:YamlPartWithRange[YValue]):YamlLocation = {
        _value = Option(n)
        _isEmpty = false
        this
    }

    def withKeyNode(n:YamlPartWithRange[YNode]):YamlLocation = {
        _keyNode = Option(n)
        _isEmpty = false
        this
    }

    def withKeyValue(n:YamlPartWithRange[YValue]):YamlLocation = {
        _keyValue = Option(n)
        _isEmpty = false
        this
    }

    def withParentStack(parentStack:List[YamlLocation]):YamlLocation = {
        this._parentStack = parentStack
        this
    }

    def inKey(position:Int):Boolean = {
        if(innerContainsPosition(position,_keyNode)){
            true
        }
        else if(_keyNode.exists(_.range.end.position == position)){
            _keyNode.get.yPart.value match {
                case sc:YScalar =>
                    var valString = sc.value
                    var start = _keyNode.get.range.start.position
                    var end = _keyNode.get.range.end.position
                    if(start + ("_"+valString).trim.length - 1 == end){
                        true
                    }
                    else {
                        false
                    }
                case _ => false
            }
        }
        else {
            false
        }

    }

    def inValue(position:Int):Boolean = {
        innerContainsPosition(position, _node) || innerContainsPosition(position, _value)
    }

    private def innerContainsPosition(pos:Int,arg:Option[YamlPartWithRange[_]]):Boolean
            = arg match {
        case Some(n) => n.containsPosition(pos)
        case None => false
    }

    def hasSameNode(another:YamlLocation):Boolean = innerSameContent(_node,another._node)

    def hasSameValue(another:YamlLocation):Boolean = innerSameContent(_value,another._value)

    private def innerSameContent(
            arg1:Option[YamlPartWithRange[_]],
            arg2:Option[YamlPartWithRange[_]]):Boolean = {

        if(arg2.isEmpty||arg1.isEmpty){
            false
        }
        else {
            arg1.get.yPart == arg2.get.yPart
        }
    }

}

object YamlLocation {

    def empty:YamlLocation = new YamlLocation()

    def apply(yPart:YPart,mapper:IPositionsMapper):YamlLocation = apply(yPart,Option(mapper), List())

    def apply(yPart:YPart,mapper:Option[IPositionsMapper]):YamlLocation = apply(yPart,mapper, List())

    def apply(yPart:YPart,mapper:IPositionsMapper, parentStack: List[YamlLocation]):YamlLocation =
        apply(yPart,Option(mapper), parentStack)

    def apply(yPart:YPart,mapper:Option[IPositionsMapper], parentStack: List[YamlLocation]):YamlLocation = {

        var result = new YamlLocation()

        yPart match {
            case mapEntry:YMapEntry =>
                val _mapEntry = YamlPartWithRange(mapEntry,mapper)
                val node = mapEntry.value
                val _node = YamlPartWithRange(node,mapper)
                val value = node.value
                val _value = YamlPartWithRange(value,mapper)
                val keyNode = mapEntry.key
                val _keyNode = YamlPartWithRange(keyNode,mapper)
                val keyValue = keyNode.value
                val _keyValue = YamlPartWithRange(keyValue,mapper)
                result.withMapEntry(_mapEntry).withNode(_node)
                  .withValue(_value).withKeyNode(_keyNode)
                  .withKeyValue(_keyValue).withParentStack(parentStack)

            case node:YNode =>
                val _node = YamlPartWithRange(node,mapper)
                val value = node.value
                val _value = YamlPartWithRange(value,mapper)
                result.withNode(_node).withValue(_value).withParentStack(parentStack)

            case value:YValue =>
                val _value = YamlPartWithRange(value,mapper)
                result.withValue(_value).withParentStack(parentStack)

            case doc:YDocument =>
                result = YamlLocation(doc.node,mapper,parentStack)

            case _ =>
        }
        result
    }
}

class YamlPartWithRange[T<:YPart] private (_yPart: T, _range:NodeRange){

    def yPart:T = _yPart

    def range:NodeRange = _range

    def containsPosition(position:Int):Boolean = _range.containsPosition(position)
}

object YamlPartWithRange {
    def apply[T<:YPart](_yPart:T, mapper:Option[IPositionsMapper]):YamlPartWithRange[T] = {
        var _range = YRange(_yPart,mapper)
        mapper.foreach(_.initRange(_range))
        new YamlPartWithRange[T](_yPart, _range)
    }
}