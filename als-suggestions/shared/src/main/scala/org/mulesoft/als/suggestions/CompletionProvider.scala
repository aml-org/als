package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.implementation.{CompletionRequest, LocationKindDetectTool, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.als.suggestions.interfaces.LocationKind._
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.positioning.{PositionsMapper, YamlLocation, YamlSearch}
import org.yaml.model.YPart

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class CompletionProvider {
    var _config:ICompletionConfig = _

    var _pluginsRegistry:ICompletionPluginsRegistry = CompletionPluginsRegistry.instance

    def withConfig(cfg:ICompletionConfig):CompletionProvider = {
        _config = cfg
        this
    }

    def suggest: Future[Seq[ISuggestion]] = suggest(true)

    def suggest(filterByPrefix:Boolean): Future[Seq[ISuggestion]] = {

        var request = composeRequest
        fulfillRequest(request).map(result=>{
            if(filterByPrefix){
                filter(result,request)
            }
            else{
                result
            }
        })
    }

    def composeRequest:ICompletionRequest = {
        var prefix: String = "";
        
        var position: Int = -1;
        
        var currentIndent: String = "";
    
        var indentCount: Int = 0;
        
        _config.editorStateProvider match {
            case Some(esp) =>
                prefix = CompletionProvider.getPrefix(esp);
                
                position = esp.getOffset;
    
                currentIndent = CompletionProvider.getCurrentIndent(esp);
    
                indentCount = CompletionProvider.getCurrentIndentCount(esp);
            case none => throw new Error("Editor state provider must be supplied")
        }

        val text = _config.editorStateProvider.get.getText
        var isJSON = text.trim.startsWith("{")
        var result = CompletionRequest(LocationKindDetectTool.determineCompletionKind(text, position), prefix, position, _config, currentIndent, indentCount);
        
        _config.astProvider match {
            case Some(ap) =>
                var ast = CompletionProvider.getAstNode(position,prefix,ap)
                var astNode = ast._1
                var astUnit = astNode.map(_.astUnit)
                var positionsMapper = astUnit.map(_.positionsMapper)
                var yamlLoaction:Option[YamlLocation] = None
                var actualYamlLoaction:Option[YamlLocation] = None
                if(positionsMapper.isDefined){
                    yamlLoaction = ast._2.map(YamlLocation(_,positionsMapper.get))
                    actualYamlLoaction = ast._2.map(YamlSearch.getLocation(position,_,positionsMapper.get,List(),isJSON))
                }
                result.withAstNode(ast._1).withYamlLocation(yamlLoaction).withActualYamlLocation(actualYamlLoaction)
            case None =>
        }
        result
    }

    def fulfillRequest(request:ICompletionRequest):Future[Seq[ISuggestion]] = {

        val filteredPlugins = _pluginsRegistry.plugins.filter(plugin=>{
            plugin.isApplicable(request)
        })

        Future.sequence(

          filteredPlugins.map(_.suggest(request))

        ).map(responses=>responses.flatMap(adjustedSuggestions))

    }

    def adjustedSuggestions(response:ICompletionResponse):Seq[ISuggestion] = {
        var isKey = response.kind == LocationKind.KEY_COMPLETION
        var isYAML = response.request.config.astProvider.exists(_.syntax == Syntax.YAML)
        var isJSON = response.request.config.astProvider.exists(_.syntax == Syntax.JSON)

        var hasQuote = false
        var hasColon = false
        var hasLine = false
        var hasKeyClosingQuote = false
        if(_config.originalContent.isDefined){
            val position = response.request.position
            val pm = PositionsMapper("original.text").withText(_config.originalContent.get)
            var lineOpt = pm.lineContainingPosition(position)
            var point = pm.point(position)
            if(lineOpt.isDefined){
                hasLine = true
                val tail = lineOpt.get.substring(point.column)
                hasQuote = tail.contains("\"")
                var colonIndex = tail.indexOf(":")
                hasColon = colonIndex >= 0
                if(colonIndex>0){
                    hasKeyClosingQuote = tail.substring(0,colonIndex).trim.endsWith("\"")
                }
                else {
                    hasKeyClosingQuote = hasQuote
                }
            }
        }

        var result = response.suggestions
        if(isYAML) {
            if (!response.noColon && isKey) {
                if(!hasLine || !hasColon) {
                    result = result.map(x => {
                        val newText = x.text + ":" + x.trailingWhitespace
                        Suggestion(newText, x.description, x.displayText, x.prefix).withCategory(x.category)
                    })
                }
            }
            else if(!isKey){
                result = result.map(x => {
                    var prefix = x.prefix
                    if(prefix == ":" && (!x.text.startsWith("\n")||x.text.startsWith("\r\n"))){
                        Suggestion(" " + x.text, x.description, x.displayText, x.prefix).withCategory(x.category)
                    }
                    else{
                        x
                    }
                })
            }
        }
        else if (isJSON) {
            var postfix = ""
            var endingQuote = false
            if(isKey){
                if(!hasKeyClosingQuote){
                    postfix += "\""
                    if(!hasColon && !response.noColon){
                        postfix += ":"
                    }
                }
//                else if (!hasColon && !response.noColon) {
//                    postfix += "\":"
//                }
                else if(!hasQuote){
                    postfix += "\""
                }
            }
            else if (!hasQuote) {
                postfix += "\""
                endingQuote = true
            }
            if (postfix.nonEmpty) {
                result = result.map(x => {
                    var isJSONObject = isJSON && x.text.startsWith("{") && x.text.endsWith("}")
                    val newText = if(!isJSONObject && (!endingQuote || !x.text.endsWith("\""))) x.text + postfix else x.text
                    Suggestion(newText, x.description, x.displayText, x.prefix).withCategory(x.category)
                })
            }
        }
        result
    }

    def filter(suggestions:Seq[ISuggestion], request:ICompletionRequest):Seq[ISuggestion] = {
        suggestions.filter(s => {
            val prefix = s.prefix.toLowerCase
            if(prefix.isEmpty || prefix == ":"){
                true
            }
            else {
                s.displayText.toLowerCase.startsWith(prefix)
            }
        });
    }
}

object CompletionProvider {

    private var prefixRegex = """(\b|['"~`!@#\$%^&*\(\)\{\}\[\]=\+,\/\?>])((\w+[\w-]*)|()|([.:;\[{\(< ]+))$""".r

    def apply():CompletionProvider = new CompletionProvider()

    def getPrefix(content: IEditorStateProvider): String = {
        val textTrim = content.getText.trim
        val isJSON = textTrim.startsWith("{") && textTrim.endsWith("}")
        var line = getLine(content)
        var opt = prefixRegex.findFirstIn(line)
        var result = opt.getOrElse("")
        if(result.startsWith("\"")){
            result = result.substring(1)
        }
        val text = content.getText
        val offset = content.getOffset
        if(offset > 0 && text.lastIndexOf("\n",offset-1)<0 && text.substring(0,offset) == "#%RAML 1.0"){
            result = ""
        }
        if(isJSON && result == "{"){
            result = ""
        }
        result
    }

    def getLine(content: IEditorStateProvider): String = {
        var offset: Int = content.getOffset
        var text: String = content.getText

        var result = ""
        var ind = text.lastIndexWhere(c => {c == '\r' || c == '\n' || c == ' ' || c == '\t'}, offset - 1)
        
        if(ind >= 0) {
            result = text.substring(ind+1, offset)
        }
        
        result
    }
    
    def getCurrentIndent(content: IEditorStateProvider): String = {
        var currentIndentation = getIndentation(content.getText, content.getOffset);
        
        if(currentIndentation.contains(" ")) {
            return "  ";
        }
    
        if(currentIndentation.contains("\t")) {
            return "\t";
        }
        
        var startIndex = getLineStart(content.getText, content.getOffset);
        
        var previousIndentation = getIndentation(content.getText, startIndex);
    
        if(previousIndentation.contains(" ")) {
            return "  ";
        }
    
        if(previousIndentation.contains("\t")) {
            return "\t";
        }
        
        "  ";
    }
    
    def getLineStart(text: String, offset: Int): Int = {
        text.lastIndexWhere(c => {c == '\r' || c == '\n'}, offset - 1);
    }
    
    def getIndentation(text: String, offset: Int): String = {
        var lineStartPosition: Int = getLineStart(text, offset);
    
        if(lineStartPosition <= 0) {
            return "";
        }
    
        var line = text.substring(lineStartPosition + 1, offset);
        
        var trimmed = line.trim;
        
        if(trimmed.length == 0) {
            line
        } else {
            var end = line.indexOf(trimmed)
    
            line.substring(0, end);
        }
    }
    
    def getCurrentIndentCount(content: IEditorStateProvider): Int = {
        var indentWidth: Int = getCurrentIndent(content).length;
        
        if(indentWidth <= 0) {
            return 0;
        }
        
        getIndentation(content.getText, content.getOffset).length / indentWidth;
    }

    def valuePrefix(content:IEditorStateProvider): String = {
        var offset = content.getOffset
        var text = content.getText
        var result = ""
        var ind = text.lastIndexWhere(c=>{
            c == '\r' || c == '\n' || c == ' ' || c == '\t' || c == '\"' || c == ''' || c == ':' || c == '('
        },offset-1)
        if(ind>=0){
            result = text.substring(ind + 1, offset)
        }
        result
    }

    def getAstNode(position:Int,prefix:String,astProvider:IASTProvider):(Option[IParseResult],Option[YPart]) = {
        var astNodeOpt = astProvider.getSelectedNode
        var yamlNodes = astNodeOpt match {
            case Some(n) => n.sourceInfo.yamlSources
            case _ => Seq()
        }
        (astNodeOpt,yamlNodes.headOption)
    }
    
    def prepareYamlContent(text:String, offset:Int): String = {
        var completionkind = LocationKindDetectTool.determineCompletionKind(text,offset);
        
        completionkind match {
            case KEY_COMPLETION | ANNOTATION_COMPLETION | SEQUENCE_KEY_COPLETION => {
                var newLineIndex = text.indexOf("\n", offset);
                
                var rightPart = if(newLineIndex < 0) {
                    text.substring(offset);
                } else {
                    text.substring(offset, newLineIndex);
                }

                val colonIndex = rightPart.indexOf(":")
                if(colonIndex < 0) {
                    text.substring(0, offset) + "k: " + text.substring(offset);
                }
                else if(colonIndex == 0){
                    text.substring(0, offset) + "k" + text.substring(offset);
                }
                else {
                    text;
                }
            }
            
            case _ =>
                if(offset==text.length){
                    text + "\n"
                }
                else {
                    text;
                }
        }
    }
    
    def prepareJsonContent(text:String, offset:Int):String = {

        var lineStart = text.lastIndexOf("\n",Math.max(0,offset-1))
        if(lineStart<0)
            lineStart = 0
        else
            lineStart += 1

        var lineEnd = text.indexOf("\n",offset)
        if(lineEnd<0)
            lineEnd = text.length

        var line = text.substring(lineStart,lineEnd)
        var off = offset - lineStart
        var lineTrim = line.trim
        var textEnding = text.substring(lineEnd+1).trim
        val hasComplexValueStartSameLine = lineTrim.endsWith("{") || lineTrim.endsWith("[")
        var hasComplexValueSameLine = hasComplexValueStartSameLine ||lineTrim.endsWith("}") ||  lineTrim.endsWith("]")
        val hasComplexValueStartNextLine = !lineTrim.endsWith(",") &&(textEnding.startsWith("{") || textEnding.startsWith("["))
        var hasComplexValueNextLine = !lineTrim.endsWith(",") & (hasComplexValueStartNextLine ||textEnding.startsWith("}") ||  textEnding.startsWith("]"))
        val hasComplexValueStart = hasComplexValueStartNextLine || hasComplexValueStartSameLine
        var needComa = !(lineTrim.endsWith(",")  || hasComplexValueNextLine || hasComplexValueSameLine)
        if(needComa) {
            val textEnding = text.substring(lineEnd).trim
            needComa = textEnding.nonEmpty && !(textEnding.startsWith(",") || textEnding.startsWith("{") || textEnding.startsWith("}") || textEnding.startsWith("[") || textEnding.startsWith("]"))
        }

        var colonIndex = line.indexOf(":")
        var newLine = line
        if(colonIndex<0){
            if(lineTrim.startsWith("\"")){
                newLine = line.substring(0,off) + "x\" : "
                if(!hasComplexValueStart){
                    newLine += "\"\""
                }
                if(!(hasComplexValueSameLine||hasComplexValueNextLine)){
                    newLine += ","
                }
            }
        }
        else if(colonIndex<=off){
            colonIndex = line.lastIndexOf(":", off)
            var substr = line.substring(colonIndex+1).trim
            var hasOpenCurlyBracket = substr.startsWith("{")
            var hasOpenSquareBracket = substr.startsWith("[")
            newLine = line.substring(0, off)
            if(hasOpenCurlyBracket||hasOpenSquareBracket){
                substr = substr.substring(1)
            }
            var hasOpenValueQuote = substr.startsWith("\"")
            var hasCloseValueQuote = line.indexOf("\"",off) >= 0

            if (!hasOpenValueQuote && !(hasOpenCurlyBracket || hasOpenSquareBracket)) {
                newLine += "\""
                hasOpenValueQuote = true
            }
            if(hasOpenValueQuote) {
                newLine += "\""
            }
            if(hasComplexValueSameLine){
                newLine += lineTrim.charAt(lineTrim.length-1)
            }
            if(lineTrim.endsWith(",")){
                newLine += ","
            }
         }
        else {
            if(line.substring(colonIndex+1).trim.startsWith("\"")){
                var openQuoteInd = line.indexOf("\"",colonIndex)
                if(off>openQuoteInd){
                    if(!lineTrim.endsWith("\"")){
                        newLine += "\""
                    }
                }
            }
            if(needComa){
                newLine += ","
            }
        }
        val result = text.substring(0,lineStart) + newLine + text.substring(lineEnd)
        result
    }

}


