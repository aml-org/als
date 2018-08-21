package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces.LocationKind
import org.mulesoft.als.suggestions.interfaces.LocationKind._
import org.mulesoft.als.suggestions.implementation.MultilineStartCheckResult._
import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.positioning.PositionsMapper
import org.mulesoft.typesystem.json.interfaces.Point

object LocationKindDetectTool {

    def determineCompletionKind(text:String, offset: Int): LocationKind = {
        var pm = PositionsMapper("file:///determine.completion.kind").withText(text)
        determineCompletionKind(pm, offset)
    }

    def determineCompletionKind(pm:IPositionsMapper, offset: Int): LocationKind = {

        if(isInsideMultilineValue(pm,offset)){
            VALUE_COMPLETION
        }
        else if(isSequenceItemStart(pm,offset)){
            SEQUENCE_KEY_COPLETION
        }
        else {
            var point = pm.point(offset)
            val column = point.column
            var posLine = pm.lineString(point.line).get
            if(checkIfAnnotation(posLine,column)){
                ANNOTATION_COMPLETION
            }
            else if(checkIfPath(posLine,column)){
                PATH_COMPLETION
            }
            else if(checkIfInVersionHeader(posLine,point)){
                VERSION_COMPLETION
            }
            else if(checkIfInComment(posLine,column)){
                INCOMMENT
            }
            else if(checkIfDirective(posLine,column)){
                DIRECTIVE_COMPLETION
            }
            else if(posLine.lastIndexOf(':', column - 1) < 0) {
                KEY_COMPLETION
            }
            else {
                VALUE_COMPLETION
            }
        }
    }

    def isSequenceItemStart(pm:IPositionsMapper,position:Int):Boolean = {

        var point = pm.point(position)
        var posLine = pm.lineString(point.line).get
        if(posLine.lastIndexOf(':',point.column)>=0){
            false
        }
        else if(posLine.trim.startsWith("-")){
            true
        }
        else if (point.line > 0){
            var offset = pm.lineOffset(posLine)
            var sequenceItemStartFound = false
            (0 until point.line).reverse.find(line => {
                var lineStrOpt = pm.lineString(line)
                lineStrOpt match {
                    case Some(lineStr) =>
                        if(lineStr.indexOf(':')>=0){
                            loop_break
                        }
                        else {
                            var lineOffset = pm.lineOffset(lineStr)
                            if (lineOffset < offset) {
                                offset = lineOffset
                                if (lineStr.trim.startsWith("-")) {
                                    sequenceItemStartFound = true
                                    loop_break
                                }
                                else {
                                    loop_continue
                                }
                            }
                            else if (lineStr.nonEmpty) {
                                loop_break
                            }
                            else {
                                loop_continue
                            }
                        }
                    case None => loop_break
                }
            })
            sequenceItemStartFound
        }
        else {
            false
        }
    }

    def isInsideMultilineValue(pm:IPositionsMapper,position:Int):Boolean = {

        var point = pm.point(position)
        if(point.line==0){
            false
        }
        else {
            var offset = pm.lineOffset(pm.lineString(point.line).get.substring(0,point.column))
            var multilineStartFound = false
            (0 until point.line).reverse.find(line => {
                var lineStrOpt = pm.lineString(line)
                lineStrOpt match {
                    case Some(lineStr) =>
                        var lineOffset = pm.lineOffset(lineStr)
                        if (lineOffset < offset) {
                            offset = lineOffset
                            isMultilineStringStart(lineStr) match {
                                case FALSE => loop_continue
                                case TRUE =>
                                    multilineStartFound = true
                                    loop_break
                                case FAIL => loop_break
                                case _ => loop_break
                            }
                        }
                        else {
                            loop_continue
                        }
                    case None => loop_break
                }
            })
            multilineStartFound
        }
    }

    private def isMultilineStringStart(str:String):MultilineStartCheckResult = {

        var end = str.length
        if(str.endsWith("\n")){
            end -= 1
        }
        if(str.endsWith("\r")){
            end -= 1
        }
        if(end==0){
            FALSE
        }
        else {
            var firstNonWhiteSpaceIndex = str.lastIndexWhere(ch => ch != ' ' && ch != '\t', end-1)
            if(firstNonWhiteSpaceIndex < 2){
                FAIL
            }
            else if(str.charAt(firstNonWhiteSpaceIndex) != '|'){
                FALSE
            }
            else {
                var secondNonWhiteSpaceIndex = str.lastIndexWhere(ch => ch != ' ' && ch != '\t'
                    , firstNonWhiteSpaceIndex - 1)
                if(secondNonWhiteSpaceIndex < 1){
                    FAIL
                }
                else if(str.charAt(secondNonWhiteSpaceIndex) != ':'){
                    FALSE
                }
                else{
                    if(str.substring(0,secondNonWhiteSpaceIndex).trim.isEmpty){
                        FALSE
                    }
                    else {
                        var extraColonindex = str.lastIndexOf(':', secondNonWhiteSpaceIndex - 1)
                        if(extraColonindex>=0){
                            FALSE
                        }
                        TRUE
                    }
                }
            }
        }
    }

    def checkIfAnnotation(_line:String, column:Int):Boolean = {

        var lineStart = 0
        var lineEnd = _line.length

        var line = _line.substring(lineStart,lineEnd)
        var openBracketIndex = line.indexOf("(",lineStart)
        if(openBracketIndex<0){
            false
        }
        else if(line.substring(0,openBracketIndex).trim.nonEmpty){
            false
        }
        else {
            var closeBracketIndex = line.indexOf(")", column)
            if(closeBracketIndex < 0){
                true
            }
            else if (line.substring(openBracketIndex, closeBracketIndex).indexOf(":") >= 0) {
                false
            }
            else {
                if (line.substring(closeBracketIndex + 1).trim.isEmpty) {
                    true
                }
                else {
                    var colonIndex = line.indexOf(":", closeBracketIndex)
                    if (colonIndex > 0 && line.substring(closeBracketIndex + 1, colonIndex).trim.isEmpty) {
                        true
                    }
                    else {
                        false
                    }
                }
            }
        }
    }

    def checkIfPath(_line:String, column:Int):Boolean = {

        var lineStart = 0
        var searchStart = column - "!include".length
        var includeIndex = _line.lastIndexOf("!include", searchStart)
        includeIndex >= 0
    }

    def checkIfDirective(_line:String, column:Int):Boolean = {

        var includePotentialIndex = _line.lastIndexOf("!i",column)
        if(includePotentialIndex>=0){
            var segment = _line.substring(includePotentialIndex,column)
            "!include".startsWith(segment)
        }
        else {
            false
        }
    }

    def checkIfInVersionHeader(lineString:String, point:Point):Boolean = {
        if(point.line != 0){
            false
        }
        else {
            lineString.startsWith("#")
        }
    }

    def checkIfInComment(_line:String, column:Int):Boolean = {
        var commentStart = _line.lastIndexOf("#",column)
        commentStart >= 0
    }

    private val loop_break:Boolean = true

    private val loop_continue:Boolean = false

}

private sealed class MultilineStartCheckResult private {}
private object MultilineStartCheckResult {

    private def apply():MultilineStartCheckResult = new MultilineStartCheckResult()

    val TRUE:MultilineStartCheckResult = MultilineStartCheckResult()

    val FALSE:MultilineStartCheckResult = MultilineStartCheckResult()

    val FAIL:MultilineStartCheckResult = MultilineStartCheckResult()
}
