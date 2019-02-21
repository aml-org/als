package org.mulesoft.als.suggestions.plugins.oas

import amf.core.remote.{Oas, Oas20, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._

import scala.concurrent.Future

class EmptyFileCompletionPlugin extends ICompletionPlugin {
    override def id: String = EmptyFileCompletionPlugin.ID

    override def languages: Seq[Vendor] = EmptyFileCompletionPlugin.supportedLanguages

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        val text = request.config.editorStateProvider.get.getText
        var tTrim = text.trim
        var suggestions:Seq[Suggestion] = Seq()
        if(tTrim.startsWith("{")&&tTrim.endsWith("}")){
            var openInd = text.indexOf("{")
            var closeInd = text.indexOf("}")
            if(request.position>openInd && request.position <= closeInd){
                val sText = "\"swagger\": \"2.0\""
                var suggestion = Suggestion(sText,"Initialize new OAS 2.0 document",sText,request.prefix)
                suggestions = List(suggestion)
            }
        }
        else {
            val sText = "swagger: '2.0'"
            var suggestion = Suggestion(sText,"Initialize new OAS 2.0 document",sText,request.prefix)
            suggestions = List(suggestion)
        }
        val response = CompletionResponse(suggestions, LocationKind.VALUE_COMPLETION, request)
        Future.successful(response)
    }

    override def isApplicable(request: ICompletionRequest): Boolean = {
        if(!request.config.astProvider.map(_.language).exists(languages.contains)){
            return false
        }
        request.config.editorStateProvider match {
            case Some(esp) =>
                val text = esp.getText
                var tTrim = text.trim
                if(tTrim.startsWith("{")&&tTrim.endsWith("}")){
                    var interior = tTrim.substring(1,tTrim.length)
                    interior.split("\n").count(_.trim.nonEmpty) == 1
                }
                else {
                    var pos = request.position
                    if(pos<0){
                        false
                    }
                    else {
                        if(pos > text.length){
                            pos = text.length
                        }
                        text.substring(0,pos).trim.isEmpty
                    }
                }
            case _ => false
        }
    }
}

object EmptyFileCompletionPlugin {

    val ID = "empty.file.completion.plugin";

    def apply(): EmptyFileCompletionPlugin = new EmptyFileCompletionPlugin();
    
    val supportedLanguages:List[Vendor] = List(Oas, Oas20);
}


