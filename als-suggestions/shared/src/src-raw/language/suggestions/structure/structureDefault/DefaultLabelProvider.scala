package org.mulesoft.als.suggestions.structure.structureDefault

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureImpl.StructureImplIndex
import org.mulesoft.als.suggestions.structure.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.structure.underscore.UnderscoreIndex
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.StructureDefaultInterfacesIndex
import org.mulesoft.als.suggestions.structure....common.commonInterfaces.CommonInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureDefault.DefaultLabelProvider;
import org.mulesoft.als.suggestions.structure.structureDefault.DefaultDecorator;

class DefaultLabelProvider {
  def getLabelText(node: IParseResult): String = {
 if (node.isAttr()) {
 var attr = node.asInstanceOf[IAttribute]
if (attr.value())
return ((attr.name()+":")+attr.value())
 
}
else if (node.isUnknown()) {
 return "Unknown"
 
}
var hlNode = node.asInstanceOf[IHighLevelNode]
if (((hlNode.definition().key()===universes.Universe08.DocumentationItem)||(hlNode.definition().key()===universes.Universe10.DocumentationItem))) {
 var titleAttribute = hlNode.attr( "title" )
if (titleAttribute) {
 return titleAttribute.value()
 
}
 
}
if ((!node.lowLevel()))
return ""
return node.name()
 
}
  def getTypeText(node: IParseResult): String = {
 if ((!node.isElement()))
return null
var hlNode = node.asInstanceOf[IHighLevelNode]
var typeAttribute = hlNode.attr( "type" )
if (typeAttribute) {
 var typeValue = typeAttribute.value()
if ((typeValue==null)) {
 (typeValue="")
 
}
var typeText = ""
if ((typeof(typeValue)==="object")) {
 (typeText=(":"+(typeValue.asInstanceOf[IStructuredValue]).valueName()))
 
}
else {
 (typeText=(":"+typeValue))
 
}
return typeText
 
}
return null
 
}
}
