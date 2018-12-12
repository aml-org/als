package org.mulesoft.als.suggestions.index

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureImpl.StructureImplIndex
import org.mulesoft.als.suggestions.structure.structureDefault.StructureDefaultIndex
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.StructureDefaultInterfacesIndex
import org.mulesoft.als.suggestions.node_details.detailsInterfaces.DetailsInterfacesIndex
import org.mulesoft.als.suggestions.node_details.detailsImpl.DetailsImplIndex
import org.mulesoft.als.suggestions.common.commonInterfaces.CommonInterfacesIndex
import org.mulesoft.als.suggestions.common.tools.ToolsIndex
import org.mulesoft.als.suggestions.common.logger.LoggerIndex

class IndexIndex{

type StructureNodeJSON = StructureNodeJSON
type StructureNode = StructureNode
type LabelProvider = LabelProvider
type Decorator = Decorator
type ContentProvider = ContentProvider
type CategoryFilter = CategoryFilter
type VisibilityFilter = VisibilityFilter
type IASTProvider = IASTProvider
type KeyProvider = KeyProvider
type Decoration = Decoration
type TypedStructureNode = TypedStructureNode
type DetailsItemJSON = DetailsItemJSON
type DetailsValuedItemJSON = DetailsValuedItemJSON
type DetailsItemWithOptionsJSON = DetailsItemWithOptionsJSON
type DetailsActionItemJSON = DetailsActionItemJSON
type DetailsItemType = DetailsItemType
type ActionItemSubType = ActionItemSubType
type DetailsItem = DetailsItem
type IEditorProvider = IEditorProvider
type IAbstractTextEditor = IAbstractTextEditor
type IEditorTextBuffer = IEditorTextBuffer
type IRange = IRange
type IPoint = IPoint
def initialize() = {
 structureDefault.initialize()
 
}
def setASTProvider(astProvider: IASTProvider): Unit = {
 tools.setASTProvider( astProvider )
 
}
def addLabelProvider(provider: LabelProvider): Unit = {
 structureImpl.addLabelProvider( provider )
 
}
def addDecorator(decorator: Decorator): Unit = {
 structureImpl.addDecorator( decorator )
 
}
def addCategoryFilter(categoryName: String, categoryFilter: CategoryFilter): Unit = {
 structureImpl.addCategoryFilter( categoryName, categoryFilter )
 
}
def setVisibilityFilter(visibilityFilter: VisibilityFilter): Unit = {
 structureImpl.setVisibilityFilter( visibilityFilter )
 
}
def setContentProvider(contentProvider: ContentProvider): Unit = {
 structureImpl.setContentProvider( contentProvider )
 
}
def setKeyProvider(keyProvider: KeyProvider): Unit = {
 structureImpl.setKeyProvider( keyProvider )
 
}
def getStructure(categoryName: String): StructureNode = {
 return structureImpl.getStructure( categoryName )
 
}
def getStructureJSON(categoryName: String): StructureNodeJSON = {
 var structureRoot = structureImpl.getStructure( categoryName )
if ((!structureRoot))
return null
return structureRoot.toJSON()
 
}
def getStructureForAllCategories(): {   def apply(categoryName: String): StructureNode
  /* def update() -- if you need it */
 } = {
 return structureImpl.getStructureForAllCategories()
 
}
def addDecoration(nodeType: NodeType, decoration: Decoration): Unit = {
 structureDefault.addDecoration( nodeType, decoration )
 
}
def isTypedStructureNode(node: StructureNode): Boolean = {
 return structureDefaultInterfaces.isTypedStructureNode( node )
 
}
def getDetails(position: Int): DetailsItem = {
 return detailsImplementation.buildItemByPosition( position )
 
}
def buildDetailsItem(node: IParseResult): DetailsItem = {
 return detailsImplementation.buildItem( node )
 
}
def getDetailsJSON(position: Int): DetailsItemJSON = {
 var detailsRoot = getDetails( position )
if ((!detailsRoot))
return null
return detailsRoot.toJSON()
 
}
def setEditorProvider(editorProvider: IEditorProvider) = {
 tools.setEditorProvider( editorProvider )
 
}
def changeDetailValue(position: Int, itemID: String, value: ( String | Int | Boolean )): IChangedDocument = {
 return detailsImplementation.changeDetailValue( position, itemID, value )
 
}
def runDetailsAction(position: Int, itemID: String): IChangedDocument = {
 return detailsImplementation.runDetailsAction( position, itemID )
 
}
def setLogger(logger: ILogger): Unit = {
 loggerModule.setLogger( logger )
 
}


}
