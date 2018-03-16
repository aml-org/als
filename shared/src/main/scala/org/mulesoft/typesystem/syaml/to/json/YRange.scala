package org.mulesoft.typesystem.syaml.to.json

import org.mulesoft.typesystem.json.interfaces.NodeRange
import org.yaml.model.{YPart, YScalar}

class YRange(_start: YPoint, _end: YPoint) extends NodeRange{

    def start: YPoint = _start

    def end: YPoint = _end

    override def toString:String = if(isEmprty) "[empty]" else s"[$start-$end]"

    def ==(other:NodeRange):Boolean = start == other.start && end == other.end

    def isEmprty:Boolean = this == YRange.empty
}

object YRange {

    val empty = YRange(YPoint(-1,-1),YPoint(-1,-1))

    def apply(_start: YPoint, _end: YPoint):YRange = new YRange(_start, _end)

    def apply(yPart:YPart):YRange = {
        val yRange = yPart.range
        val startLine = yRange.lineFrom - 1
        val startColumn = yRange.columnFrom
        val endLine = yRange.lineTo - 1
        var endColumn = yRange.columnTo
        if(yPart.isInstanceOf[YScalar]){
            endColumn += 1
        }
        val startPosition = YPoint(startLine, startColumn)
        val endPosition = YPoint(endLine, endColumn)
        YRange(startPosition,endPosition)
    }
}
