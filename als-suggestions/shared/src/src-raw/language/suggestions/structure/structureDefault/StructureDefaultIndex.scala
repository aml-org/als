package org.mulesoft.als.suggestions.structure.structureDefault

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureImpl.StructureImplIndex
import org.mulesoft.als.suggestions.structure.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.structure.underscore.UnderscoreIndex
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.StructureDefaultInterfacesIndex
import org.mulesoft.als.suggestions.structure....common.commonInterfaces.CommonInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureDefault.DefaultLabelProvider;
import org.mulesoft.als.suggestions.structure.structureDefault.DefaultDecorator;

class StructureDefaultIndex{

var _defaultDecorator = new DefaultDecorator()
var _defaultLabelProvider = new DefaultLabelProvider()
def addDecoration(nodeType: NodeType, decoration: Decoration): Unit = {
 _defaultDecorator.addDecoration( nodeType, decoration )
 
}
def DefaultKeyProvider(node: IParseResult): String = {
 if ((!node))
return null
if ((node&&(!node.parent()))) {
 return node.name()
 
}
else {
 return ((node.name()+" :: ")+DefaultKeyProvider( node.parent() ))
 
}
 
}
def DefaultVisibilityFilter(node: IParseResult): Boolean = {
 return true
 
}
def initialize() = {
 structureImpl.setKeyProvider( DefaultKeyProvider )
structureImpl.addLabelProvider( _defaultLabelProvider )
structureImpl.addDecorator( _defaultDecorator )
structureImpl.setVisibilityFilter( DefaultVisibilityFilter )
 
}


}
