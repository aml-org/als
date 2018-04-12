package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}

import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion, LocationKind}
import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition

import org.yaml.model.YScalar

import scala.concurrent.{Future, Promise}

class ExampleCompletionPlugin extends ICompletionPlugin {
	override def id: String = ExampleCompletionPlugin.ID;
	
	override def languages: Seq[Vendor] = FacetsCompletionPlugin.supportedLanguages;
	
	override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {
		case Some(astProvider) => languages.indexOf(astProvider.language) >= 0 && isFullRequest(request) && isInKey(request) && isExample(request);
		
		case _ => false;
	}
	
	def isFullRequest(request: ICompletionRequest): Boolean = {
		request.astNode.isDefined && request.astNode.get != null && request.astNode.get.property.isDefined && request.astNode.get.property.get != null;
	}
	
	def isInKey(request: ICompletionRequest): Boolean = {
		request.kind == LocationKind.KEY_COMPLETION;
	}
	
	override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = Promise.successful(findProperties(extractAstPath(request), extractLocalType(request)).map(propertyName => Suggestion(propertyName, id, propertyName, request.prefix))).future
	
	def findProperties(path: Seq[String], localType: ITypeDefinition): Seq[String] = if(localType == null) {
		Seq();
	} else if(path.isEmpty) {
		localType.allProperties.map(_.nameId.get);
	} else localType.property(path.last) match {
		case Some(property) => findProperties(path.dropRight(1), property.range.get);
		
		case _ => Seq();
	}
	
	def extractLocalType(request: ICompletionRequest): ITypeDefinition = request.astNode.get.parent match {
		case Some(parent) => parent.parent match {
			case Some(typeDeclaration) => typeDeclaration.localType match {
				case Some(localType) => localType
				
				case _ => null
			}
			
			case _ => null;
		}
		
		case _ => null;
	}
	
	def extractAstPath(request: ICompletionRequest): Seq[String] = request.actualYamlLocation.get.parentStack.filter(_.keyValue.isDefined).map(_.keyValue.get.yPart).filter(_ match {
		case n: YScalar => true;
		
		case _ => false
	}).map(_.asInstanceOf[YScalar].text);
	
	def isExample(request: ICompletionRequest): Boolean = request.astNode.get.property.get.domain match {
		case Some(domain) => domain.nameId.get == "ExampleSpec";
		
		case _ => false;
	}
}

object ExampleCompletionPlugin {
	val ID = "example.completion";
	
	val supportedLanguages: List[Vendor] = List(Raml10);
	
	def apply(): ExampleCompletionPlugin = new ExampleCompletionPlugin();
}