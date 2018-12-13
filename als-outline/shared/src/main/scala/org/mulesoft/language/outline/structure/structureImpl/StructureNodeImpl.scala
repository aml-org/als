package org.mulesoft.language.outline.structure.structureImpl

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.structure.structureInterfaces.{StructureNode, StructureNodeJSON}

import scala.collection.mutable.ArrayBuffer

class StructureNodeImpl (private var hlSource: IParseResult) extends StructureNode {

  var text: String = ""
  var typeText: Option[String] = Some("")
  var icon: String = ""
  var textStyle: String = ""
  var children: Seq[StructureNode] = ArrayBuffer()
  var key: String = ""
  var start: Int = 0
  var end: Int = 0
  var selected: Boolean = false
  var category: String = ""


  def getSource: IParseResult = {

    this.hlSource
  }

  def toJSON: StructureNodeJSON = {

    val instance = this

    new StructureNodeJSON {

      override def icon: String = instance.icon

      override def start: Int = instance.start

      override def children: Seq[StructureNodeJSON] = instance.children.map(child=>child.toJSON)

      override def typeText: Option[String] = instance.typeText

      override def end: Int = instance.end

      override def textStyle: String = instance.textStyle

      def text: String = instance.text

      override def category: String = instance.category

      override def selected: Boolean = instance.selected

      override def key: String = instance.key
    }
  }
}