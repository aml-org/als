package org.mulesoft.als.suggestions.plugins.oas

import amf.core.remote.{Oas, Oas2, Oas2Yaml, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}

import scala.concurrent.Future

class EmptyFileCompletionPlugin extends ICompletionPlugin {
    override def id: String = EmptyFileCompletionPlugin.ID

    override def languages: Seq[Vendor] = EmptyFileCompletionPlugin.supportedLanguages

    override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {

        val text = request.config.editorStateProvider.get.getText
        var tTrim = text.trim
        if(tTrim.startsWith("{")&&tTrim.endsWith("}")){
            var openInd = text.indexOf("{")
            var closeInd = text.indexOf("}")
            if(request.position>openInd && request.position <= closeInd){
                val sText = "\"swagger\": \"2.0\""
                var suggestion = Suggestion(sText,id,sText,request.prefix)
                Future.successful(List(suggestion))
            }
            else {
                Future.successful(Seq())
            }
        }
        else {
            val sText = "swagger: '2.0'"
            var suggestion = Suggestion(sText,id,sText,request.prefix)
            Future.successful(List(suggestion))
        }
    }

    override def isApplicable(request: ICompletionRequest): Boolean = request.config.editorStateProvider match {
        case Some(esp) =>
            val text = esp.getText
            var tTrim = text.trim
            if(tTrim.startsWith("{")&&tTrim.endsWith("}")){
                var interior = tTrim.substring(1,tTrim.length)
                interior.split("\n").count(_.trim.nonEmpty) == 1
            }
            else {
                text.split("\n").count(_.trim.nonEmpty) == 1
            }
        case _ => false
    }
}

object EmptyFileCompletionPlugin {

    val ID = "empty.file.completion.plugin";

    def apply(): EmptyFileCompletionPlugin = new EmptyFileCompletionPlugin();
    
    val supportedLanguages:List[Vendor] = List(Oas, Oas2, Oas2Yaml);
}


