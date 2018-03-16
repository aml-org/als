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

        var prefix:String = ""
        var position:Int = -1

        _config.editorStateProvider match {
            case Some(esp) =>
                prefix = CompletionProvider.getPrefix(esp)
                position = esp.getOffset
            case none => throw new Error("Editor state provider must be supplied")
        }
        var result = CompletionRequest(requestKind,prefix,position,_config)
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

    def fulfillRequest(request:ICompletionRequest):Future[Seq[ISuggestion]] = Future {
        _pluginsRegistry.plugins.flatMap(_.suggest(request))
    }

    def filter(suggestions:Seq[ISuggestion], request:ICompletionRequest):Seq[ISuggestion] = {
        suggestions.filter(s => s.displayText.startsWith(s.prefix));
    }

    def requestKind:CompletionRequestKind = CompletionRequestKind.PROPERTY_NAMES
}

object CompletionProvider {

    private var prefixRegex = """(\b|['"~`!@#\$%^&*\(\)\{\}\[\]=\+,\/\?>])((\w+[\w-]*)|([.:;\[{\(< ]+))$""".r

    def apply():CompletionProvider = new CompletionProvider()

    def getPrefix(content: IEditorStateProvider): String = {
        var line = getLine(content)
        var opt = prefixRegex.findFirstIn(line)
        opt.getOrElse("")
    }

    def getLine(content:IEditorStateProvider): String = {

        var offset: Int = content.getOffset
        var text: String = content.getText

        var result = ""
        var ind = text.lastIndexWhere(c=>{c == '\r' || c == '\n' || c == ' ' || c == '\t'},offset-1)
        if(ind>=0){
            result = text.substring(ind+1,offset)
        }
        result
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

}


