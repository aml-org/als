package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml08, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._

import scala.concurrent.Future

class EmptyRamlFileCompletionPlugin extends ICompletionPlugin {
    override def id: String = EmptyRamlFileCompletionPlugin.ID

    override def languages: Seq[Vendor] = EmptyRamlFileCompletionPlugin.supportedLanguages

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        val text = request.config.editorStateProvider.get.getText
        var str = text.substring(0,request.position)
        var suggestions:Seq[Suggestion] = Seq()
        var prefix = request.prefix
        if("#%RAML".startsWith(str) && "#%RAML".length > str.length){
            prefix = str
            suggestions = List[String]("#%RAML 1.0", "#%RAML 0.8").map(x=>Suggestion(x,s"Initialize new $x document",x,prefix))
        }
        else if(str.startsWith("#%RAML")){
            var ind = str.lastIndexOf(" ")
            if(str == "#%RAML"){
                suggestions = List[String](" 1.0", " 0.8").map(x=>Suggestion(x,s"Initialize new RAML$x document",x,prefix))
            }
            else if(str.length < "#%RAML 1.0".length){
                if(prefix == "."){
                    prefix = str.substring(str.length-2,str.length)
                }
                suggestions = List[String]("1.0", "0.8").map(x=>Suggestion(x,s"Initialize new RAML $x document",x,prefix))
            }
            else if(str == "#%RAML 1.0"){
                suggestions = EmptyRamlFileCompletionPlugin.fragmentNames.map(x=>Suggestion(" " + x,"Fragment header",x,prefix))
            }
            else if(str.startsWith("#%RAML 1.0")){
                suggestions = EmptyRamlFileCompletionPlugin.fragmentNames.map(x=>Suggestion(x,"Fragment header",x,prefix))
            }
        }
        val response = CompletionResponse(suggestions, LocationKind.VALUE_COMPLETION, request)
        Future.successful(response)
    }

    override def isApplicable(request: ICompletionRequest): Boolean = {

        var text = request.config.editorStateProvider.get.getText
        val trimmed = text.trim
        if(trimmed.length>0){
            if(!(trimmed.startsWith("#%RAML") || "#%RAML".startsWith(trimmed))){
                return false
            }
        }
        var ind = text.indexOf("\n")
        if(ind < 0){
            ind = text.length
        }
        if(request.position < 0 || request.position > ind){
            return false;
        }
        request.config.editorStateProvider match {
            case Some(esp) =>
                val text = esp.getText.substring(0,esp.getOffset)
                var tTrim = text.trim
                text.split("\n").count(_.trim.nonEmpty) <= 1
            case _ => false
        }
    }
}

object EmptyRamlFileCompletionPlugin {

    val ID = "empty.raml.file.completion.plugin";

    def apply(): EmptyRamlFileCompletionPlugin = new EmptyRamlFileCompletionPlugin();

    val supportedLanguages:List[Vendor] = List(Raml10, Raml08);

    val fragmentNames:List[String] = List("ResourceType", "Trait", "AnnotationTypeDeclaration", "DataType", "DocumentationItem", "NamedExample", "Extension", "SecurityScheme", "Overlay", "Library")
}




