package org.mulesoft.positioning

import org.mulesoft.typesystem.json.interfaces.{NodeRange, Point}

trait IPositionsMapper{

    def initRange(range:NodeRange):Unit

    def initPoint(point:Point):Unit

    def mapToPosition(line:Int,colum:Int):Int

    def offset(position:Int):Int

    def lineOffset(str:String):Int

    def point(position: Int): Point

    def lineString(line: Int): Option[String]

    def getText:String

    def textLength:Int

    def line(lineIndex:Int):Option[String]

    def lineContainingPosition(position:Int):Option[String]
}
