package org.mulesoft.als.suggestions.structure.structureDefault

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureImpl.StructureImplIndex
import org.mulesoft.als.suggestions.structure.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.structure.underscore.UnderscoreIndex
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.StructureDefaultInterfacesIndex
import org.mulesoft.als.suggestions.structure....common.commonInterfaces.CommonInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureDefault.DefaultLabelProvider;
import org.mulesoft.als.suggestions.structure.structureDefault.DefaultDecorator;

class DefaultDecorator extends CommonInterfacesIndex.Decorator {
  var decorations: {   def apply(nodeType: Int): Decoration
  /* def update() -- if you need it */
 } = Map(
)
  def addDecoration(nodeType: NodeType, decoration: Decoration): Unit = {
 (this.decorations(nodeType)=decoration)
 
}
  def getNodeType(node: IParseResult): NodeType = {
 if (node.isAttr()) {
 return defaultInterfaces.NodeType.ATTRIBUTE
 
}
else if (node.isUnknown()) {
 return defaultInterfaces.NodeType.UNKNOWN
 
}
var hlNode = node.asInstanceOf[IHighLevelNode]
var nodeDefinition = hlNode.definition().key()
if (((nodeDefinition==universes.Universe08.Resource)||(nodeDefinition===universes.Universe10.Resource))) {
 return defaultInterfaces.NodeType.RESOURCE
 
}
else if (((nodeDefinition===universes.Universe08.Method)||(nodeDefinition===universes.Universe10.Method))) {
 return defaultInterfaces.NodeType.METHOD
 
}
else if (((nodeDefinition===universes.Universe08.AbstractSecurityScheme)||(nodeDefinition===universes.Universe10.AbstractSecurityScheme))) {
 return defaultInterfaces.NodeType.SECURITY_SCHEME
 
}
else if (((nodeDefinition==universes.Universe10.TypeDeclaration)&&universeHelpers.isAnnotationTypesProperty( node.property() ))) {
 return defaultInterfaces.NodeType.ANNOTATION_DECLARATION
 
}
else if ((hlNode.definition().isAssignableFrom( universes.Universe10.TypeDeclaration.name )||hlNode.definition().isAssignableFrom( universes.Universe08.Parameter.name ))) {
 return defaultInterfaces.NodeType.TYPE_DECLARATION
 
}
else if (((nodeDefinition===universes.Universe08.DocumentationItem)||(nodeDefinition===universes.Universe10.DocumentationItem))) {
 return defaultInterfaces.NodeType.DOCUMENTATION_ITEM
 
}
if ((node.lowLevel().unit()!=node.root().lowLevel().unit())) {
 return defaultInterfaces.NodeType.EXTERNAL_UNIT
 
}
return defaultInterfaces.NodeType.OTHER
 
}
  def getDecoration(node: IParseResult): Decoration = {
 var nodeType = this.getNodeType( node )
if ((!nodeType))
return null
return this.decorations(nodeType)
 
}
  def getIcon(node: IParseResult): String = {
 var decoration = this.getDecoration( node )
if ((!decoration))
return null
return decoration.icon
 
}
  def getTextStyle(node: IParseResult): String = {
 var decoration = this.getDecoration( node )
if ((!decoration))
return null
return decoration.textStyle
 
}
}
