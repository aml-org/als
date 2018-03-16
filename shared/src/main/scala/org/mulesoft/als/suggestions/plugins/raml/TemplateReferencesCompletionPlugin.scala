package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.high.level.Search

import scala.collection.mutable

class TemplateReferencesCompletionPlugin extends ICompletionPlugin {

    override def id: String = TemplateReferencesCompletionPlugin.ID

    override def languages: Seq[Vendor] = TemplateReferencesCompletionPlugin.supportedLanguages

    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {

        case Some(astProvider) =>
            languages.indexOf(astProvider.language)>=0
        case _ => false
    }

    override def suggest(request: ICompletionRequest): Seq[ISuggestion] = {

        if(!isApplicable(request)){
            Seq()
        }
        else {
            var result: Seq[ISuggestion] = Seq();
			//var range: YRange = YRange(YPoint(21, 2), YPoint(23, 0));
			//request.astNode.get.astUnit.positionsMapper.initRange(range);
			
            request.astNode match {
                case Some(n) =>
					if(n.isElement) {
						var actualPrefix = request.prefix;
						
						var squareBracketsRequired = false;
						
						var declarations = n.asElement.get.definition.nameId match {
							case Some("ResourceTypeRef")  => {
								Search.getDeclarations(n.astUnit, "ResourceType");
							}
							
							case Some("TraitRef")  => {
								squareBracketsRequired = true;
								
								Search.getDeclarations(n.astUnit, "Trait");
							}
							
							case _=> Seq();
						}
						
						result = declarations.map(declaration => {
							var declarationName = declaration.node.attribute("name").get.value.asInstanceOf[Some[String]].get
							
							var bracketsRequired = request.prefix.indexOf("{") != 0;
							
							squareBracketsRequired = squareBracketsRequired && request.prefix.indexOf("[") != 0;
							
							var snippet = declarationName
							
							var params = declaration.node.attributes("parameters");
							
							if(!params.isEmpty) {
								snippet = snippet + ": {";
								
								var count = 0;
								
								params.foreach(param => {
									var needComma = param != params.last;
									
									snippet += param.value.asInstanceOf[Some[String]].get + " : ";
									
									if(needComma) {
										snippet += ", ";
									}
								})
								
								snippet += "}";
							}
							
							if(bracketsRequired) {
								snippet = "{" + snippet + "}";
							} else {
								actualPrefix = actualPrefix.substring(actualPrefix.indexOf("{") + 1).trim();
							}
							
							if(squareBracketsRequired) {
								snippet = "[" + snippet + "]";
							} else {
								actualPrefix = actualPrefix.substring(actualPrefix.indexOf("[") + 1).trim();
							}
							
							Suggestion(snippet, id, declarationName, actualPrefix);
						})
                    } else if(n.isAttr) {
						
					}
                case _ =>
            }
            result
        }
    }
}

object TemplateReferencesCompletionPlugin {
	val ID = "templateRef.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): TemplateReferencesCompletionPlugin = new TemplateReferencesCompletionPlugin();
}