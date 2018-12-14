//package org.mulesoft.als.outline.common.tools
//
//import org.mulesoft.als.outline.common.commonInterfaces.CommonInterfacesIndex
//import org.mulesoft.als.outline.common.raml_1_parser.Raml1ParserIndex
//import org.mulesoft.als.outline.common.tools.EditorBasedASTProvider;
//
//class ToolsIndex{
//
//var _astProvider: IASTProvider = null
//var _editorProvider: IEditorProvider = null
//def setASTProvider(astProvider: IASTProvider) = {
// (_astProvider=astProvider)
//
//}
//def setEditorProvider(editorProvider: IEditorProvider) = {
// (_editorProvider=editorProvider)
//
//}
//def getRootNode(): IHighLevelNode = {
// if (_astProvider)
//return _astProvider.getASTRoot()
//if ((!_editorProvider))
//return null
//return (new EditorBasedASTProvider( _editorProvider )).getASTRoot()
//
//}
//def getCurrentNode(position: Int): IParseResult = {
// if (_astProvider) {
// var root = _astProvider.getASTRoot()
//if ((root&&(position!=null)))
//return root.findElementAtOffset( position )
//var astProviderSelectedNode = _astProvider.getSelectedNode()
//if (astProviderSelectedNode)
//return astProviderSelectedNode
//
//}
//if (_editorProvider)
//return (new EditorBasedASTProvider( _editorProvider )).getASTRoot()
//return null
//
//}
//def basename(path: String): String = {
// var delimiterIndex = (-1)
//(delimiterIndex=path.lastIndexOf( "\\" ))
//if ((delimiterIndex==(-1)))
//(delimiterIndex=path.lastIndexOf( "/" ))
//return (if (((delimiterIndex+1)<path.length)) path.substring( (delimiterIndex+1) ) else "")
//
//}
//def dirname(path: String): String = {
// var delimiterIndex = (-1)
//(delimiterIndex=path.lastIndexOf( "\\" ))
//if ((delimiterIndex==(-1)))
//(delimiterIndex=path.lastIndexOf( "/" ))
//return path.substring( 0, (delimiterIndex-1) )
//
//}
//
//
//}
