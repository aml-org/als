package org.mulesoft.als.suggestions.plugins.raml

//import java.io.File
//import java.net.URI

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.yaml.model.YScalar

class MasterReferenceCompletionPlugin extends ICompletionPlugin {
    override def id: String = TemplateReferencesCompletionPlugin.ID;
	
    override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages;
	
    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && isExtendable(request) && isInExtendsProperty(request);
		
		case _ => false;
    }
	
    override def suggest(request: ICompletionRequest): Seq[ISuggestion] = {
		//TODO: fix fs access using fsprovider
//		var fsProvider = request.astNode.get.astUnit.project.fsProvider;
//
//		var baseDir = new File(request.astNode.get.astUnit.project.rootPath).getParent + "/";
//
//		var files = new File(baseDir).list().filter(_.toLowerCase.endsWith(".raml"));
//
//		files.map(name => Suggestion(name, id, name, request.prefix));
		
		Seq();
    }
	
	def isExtendable(request: ICompletionRequest): Boolean = {
		request.astNode.get.isElement && (request.astNode.get.asElement.get.definition.isAssignableFrom("Overlay") || request.astNode.get.asElement.get.definition.isAssignableFrom("Extension"));
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