package org.mulesoft.typesystem.syaml.to.json

import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.json.interfaces.NodeRange
import org.yaml.model._

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

    private def apply(yPart:YPart):YRange = {
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

    def apply(yPart: YPart, mapperOpt: Option[IPositionsMapper]): YRange = {
        if(mapperOpt.isEmpty){
            YRange(yPart)
        }
        else {
            val pm = mapperOpt.get
            val yRange = yPart.range
            val startLine = yRange.lineFrom - 1
            val startColumn = yRange.columnFrom
            val startPosition = pm.mapToPosition(startLine, startColumn)

            val endLine = yRange.lineTo - 1
            var endColumn = yRange.columnTo
            val scalarOpt:Option[YScalar] = yPart match {
                case sc: YScalar => Option(sc)
                case me: YMapEntry => me.value match {
                    case n: YNode => n.value match {
                        case sc: YScalar => Option(sc)
                        case _ => None
                    }
                }
                case n: YNode => n.value match {
                    case sc: YScalar => Option(sc)
                    case _ => None
                }
                case _ => None
            }
            if (scalarOpt.nonEmpty) {
                val mark = scalarOpt.get.mark
                if(mark != DoubleQuoteMark && mark != SingleQuoteMark) {
                    pm.line(endLine).foreach(line => {
                        if(line.substring(endColumn).trim.isEmpty) {
                            while (endColumn < line.length && line.charAt(endColumn) == ' ') {
                                endColumn += 1
                            }
                        }
                        endColumn += 1
                    })
                }
            }
            var endPosition = pm.mapToPosition(endLine, endColumn)
            val startPoint = YPoint(startLine, startColumn, startPosition)
            val endPoint = YPoint(endLine, endColumn, endPosition)
            YRange(startPoint, endPoint)
        }
    }
}
