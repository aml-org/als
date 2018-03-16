package org.mulesoft.als.suggestions.structure.structureImpl

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.structure.underscore.UnderscoreIndex
import org.mulesoft.als.suggestions.structure....common.commonInterfaces.CommonInterfacesIndex
import org.mulesoft.als.suggestions.structure....common.tools.ToolsIndex
import org.mulesoft.als.suggestions.structure.structureImpl.StructureNodeImpl;

class StructureNodeImpl extends StructureInterfacesIndex.StructureNode {
  var text: String = _
  var typeText: String = _
  var icon: String = _
  var textStyle: String = _
  var children: Array[StructureNodeImpl] = _
  var key: String = _
  var start: Int = _
  var end: Int = _
  var selected: Boolean = _
  var category: String = _
  def this(hlSource: IParseResult) = {
}
  def getSource(): IParseResult = {
 return this.hlSource
 
}
  def toJSON(): StructureNodeJSON = {
 var result: StructureNodeJSON = Map( "text" -> this.text,
"typeText" -> this.typeText,
"icon" -> this.icon,
"textStyle" -> this.textStyle,
"children" -> Array(),
"key" -> this.key,
"start" -> this.start,
"end" -> this.end,
"selected" -> this.selected,
"category" -> this.category )
if (this.children) {
 this.children.forEach( (child =>  {
 result.children.push( child.toJSON() )
 
}) )
 
}
return result
 
}
}
