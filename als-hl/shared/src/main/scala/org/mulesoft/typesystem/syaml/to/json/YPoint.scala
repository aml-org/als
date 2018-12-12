package org.mulesoft.typesystem.syaml.to.json

import org.mulesoft.typesystem.json.interfaces.Point

class  YPoint(_line: Int, _column: Int) extends Point {

    def this(_line: Int, _column: Int, pos: Int) = {
        this(_line, _column)
        _position = pos
    }

    var _position:Int = -1

    def line: Int = _line

    def column: Int = _column

    def position: Int = _position

    def setPosition(p:Int):Unit = _position = p

    override def toString:String = s"(l:$line,c:$column,p:$position)"

    def ==(other:Point):Boolean = line == other.line && column == other.column && position == other.position
}

object YPoint {
    def apply(_line: Int, _column: Int): YPoint = new YPoint(_line, _column)

    def apply(_line: Int, _column: Int, _position: Int): YPoint = new YPoint(_line, _column, _position)
}
