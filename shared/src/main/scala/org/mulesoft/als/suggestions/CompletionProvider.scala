package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.implementation.{CompletionRequest, LocationKindDetectTool}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.als.suggestions.interfaces.LocationKind._
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.positioning.{YamlLocation, YamlSearch}
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
        
        var result = CompletionRequest(requestKind, prefix, position, _config, currentIndent, indentCount);
        
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
                    actualYamlLoaction = ast._2.map(YamlSearch.getLocation(position,_,positionsMapper.get))
                }
                result.withAstNode(ast._1).withYamlLocation(yamlLoaction).withActualYamlLocation(actualYamlLoaction)
            case None => throw new Error("AST provider must be supplied")
        }
        result
    }

    def fulfillRequest(request:ICompletionRequest):Future[Seq[ISuggestion]] = {

        val filteredPlugins = _pluginsRegistry.plugins.filter(plugin=>{
            plugin.isApplicable(request)
        })

        Future.sequence(

          filteredPlugins.map(_.suggest(request))

        ).map(suggestions=>suggestions.flatten)

    }

    def filter(suggestions:Seq[ISuggestion], request:ICompletionRequest):Seq[ISuggestion] = {
        suggestions.filter(s => s.displayText.toLowerCase.startsWith(s.prefix.toLowerCase));
    }

    def requestKind:CompletionRequestKind = CompletionRequestKind.PROPERTY_NAMES
}

object CompletionProvider {

    private var prefixRegex = """(\b|['"~`!@#\$%^&*\(\)\{\}\[\]=\+,\/\?>])((\w+[\w-]*)|([.:;\[{\(< ]+))$""".r

    def apply():CompletionProvider = new CompletionProvider()

    def getPrefix(content: IEditorStateProvider): String = {
        var line = getLine(content)
        var opt = prefixRegex.findFirstIn(line)
        var result = opt.getOrElse("")
        if(result.startsWith("\"")){
            result = result.substring(1)
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
        
        var startIndex = getLineStart(content.getText, content.getOffset);
        
        var previousIndentation = getIndentation(content.getText, startIndex);
        
        var count = currentIndentation.length - previousIndentation.length;
        
        if(count < 0) {
            count = -count;
        }
        
        currentIndentation.substring(0, count);
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

    def prepareYamlContent(text:String, offset:Int):String = {

        var completionkind = LocationKindDetectTool.determineCompletionKind(text,offset)
        completionkind match {
            case KEY_COMPLETION =>
                text.substring(0,offset) + "k:" + text.substring(offset)

            case _ => text
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

        var needComa = !(lineTrim.endsWith(",") || lineTrim.endsWith("{") || lineTrim.endsWith("}") || lineTrim.endsWith("[") || lineTrim.endsWith("]"))
        if(needComa) {
            val textEnding = text.substring(lineEnd).trim
            needComa = textEnding.nonEmpty && !(textEnding.startsWith(",") || textEnding.startsWith("{") || textEnding.startsWith("}") || textEnding.startsWith("[") || textEnding.startsWith("]"))
        }

        var colonIndex = line.indexOf(":")
        var newLine = line
        if(colonIndex<0){
            if(lineTrim.startsWith("\"")){
                var openQuoteInd = line.indexOf("\"")
                if(openQuoteInd>=0 && off>openQuoteInd){
                    if(!line.substring(openQuoteInd+1).trim.endsWith("\"")){
                        newLine += "\""
                    }
                    newLine += ": \"\""
                }
            }
        }
        else if(colonIndex<=off){

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
        }
        if(needComa){
            newLine += ","
        }
        val result = text.substring(0,lineStart) + newLine + text.substring(lineEnd)
        result
    }

}


