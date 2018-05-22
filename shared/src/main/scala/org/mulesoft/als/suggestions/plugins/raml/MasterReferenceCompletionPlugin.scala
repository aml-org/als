package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{PathCompletion, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.yaml.model.YScalar

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

class MasterReferenceCompletionPlugin extends ICompletionPlugin {
    override def id: String = MasterReferenceCompletionPlugin.ID;
	
    override def languages: Seq[Vendor] = MasterReferenceCompletionPlugin.supportedLanguages;
	
    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && isExtendable(request) && isInExtendsProperty(request);
		
		case _ => false;
    }
	
    override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {

			val baseDir = request.astNode.get.astUnit.project.rootPath

			val relativePath = request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar].text

			if (!relativePath.endsWith(request.prefix)) {

				Promise.successful(Seq[ISuggestion]()).future
			} else {

				val diff = relativePath.length - request.prefix.length

				PathCompletion.complete(baseDir, relativePath, request.config.fsProvider.get)
					.map(paths=>{
						paths.map(path=>{

							val pathStartingWithPrefix = if(diff != 0) path.substring(diff) else path

							Suggestion(pathStartingWithPrefix, id,
								pathStartingWithPrefix, request.prefix)
						})
					})
			}
    }
	
	def isExtendable(request: ICompletionRequest): Boolean = {
        request.astNode.nonEmpty && request.astNode.get.isElement && (request.astNode.get.asElement.get.definition.isAssignableFrom("Overlay") || request.astNode.get.asElement.get.definition.isAssignableFrom("Extension"));
	}
	
	def isInExtendsProperty(request: ICompletionRequest): Boolean = {
		if(request.actualYamlLocation.get == null) {
			return false;
		}
		
		if(request.actualYamlLocation.get.keyValue.get == null) {
			return false;
		}
		
		request.actualYamlLocation.get.keyValue.get.yPart.asInstanceOf[YScalar].text == "extends";
	}
}

object MasterReferenceCompletionPlugin {
	val ID = "masterRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): MasterReferenceCompletionPlugin = new MasterReferenceCompletionPlugin();
}