//package org.mulesoft.language.server.common.textEditProcessor
//
//import org.mulesoft.language.server.common.typeInterfaces.ITextEdit;
//
//class TextEditProcessorIndex{
//
//def applyDocumentEdit(oldContents: String, edit: ITextEdit): String = {
// if ((edit.range.end===0)) {
// return (edit.text+oldContents)
//
//}
//if ((edit.range.start>=oldContents.length)) {
// return (oldContents+edit.text)
//
//}
//if (((edit.range.start<0)||(edit.range.end>oldContents.length))) {
// throw new Error( ((((("Range of ["+edit.range.start)+":")+edit.range.end)+"] is not applicable to document of length ")+oldContents.length) )
//}
//if ((edit.range.start>=edit.range.end)) {
// throw new Error( (((("Range of ["+edit.range.start)+":")+edit.range.end)+"] should have end greater than start") )
//}
//var beginning = ""
//if ((edit.range.start>0)) {
// (beginning=oldContents.substring( 0, (edit.range.start-1) ))
//
//}
//var end = ""
//if ((edit.range.end<oldContents.length)) {
// (end=oldContents.substring( edit.range.end ))
//
//}
//return ((beginning+edit.text)+end)
//
//}
//def applyDocumentEdits(oldContents: String, edits: Array[ITextEdit]): String = {
// if ((edits.length>1)) {
// throw new Error( "Unsupported application of more than 1 text editor at once to a single file" )
//}
//val newContents = applyDocumentEdit( oldContents, edits(0) )
//return newContents
//
//}
//
//
//}
