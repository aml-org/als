//package org.mulesoft.als.outline.common.tools
//
//import org.mulesoft.als.outline.common.commonInterfaces.CommonInterfacesIndex
//import org.mulesoft.als.outline.common.raml_1_parser.Raml1ParserIndex
//import org.mulesoft.als.outline.common.tools.EditorBasedASTProvider;
//
//class EditorBasedASTProvider extends CommonInterfacesIndex.IASTProvider {
//  def this(editorProvider: IEditorProvider) = {
//}
//  def getASTRoot(): IHighLevelNode = {
// var editor = this.editorProvider.getCurrentEditor()
//if ((!editor))
//return null
//var filePath = editor.getPath()
//var prj = parser.project.createProject( dirname( filePath ) )
//var offset = editor.getBuffer().characterIndexForPosition( editor.getCursorBufferPosition() )
//var text = editor.getBuffer().getText()
//var unit = prj.setCachedUnitContent( basename( filePath ), text )
//return unit.highLevel().asInstanceOf[IHighLevelNode]
//
//}
//  def getNodeByPosition(): IParseResult = {
// var editor = this.editorProvider.getCurrentEditor()
//if ((!editor))
//return null
//var ast = this.getASTRoot()
//if ((!ast))
//return null
//var offset = editor.getBuffer().characterIndexForPosition( editor.getCursorBufferPosition() )
//var modifiedOffset = offset
//var text = editor.getText()
//{
//var currentOffset = (offset-1)
//while( (currentOffset>=0)) {
// {
// var currentCharacter = text(currentOffset)
//if (((currentCharacter==" ")||(currentCharacter=="\t"))) {
// (modifiedOffset=(currentOffset-1))
//continue
//
//}
//break()
//
//}
// (currentOffset-= 1)
//}
//}
//var astNode = ast.findElementAtOffset( modifiedOffset )
//if ((!astNode)) {
// return ast
//
//}
//return astNode
//
//}
//}
