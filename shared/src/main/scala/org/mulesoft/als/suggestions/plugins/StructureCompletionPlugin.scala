package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces.{ICompletionPlugin, ICompletionRequest, ISuggestion}
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition}

import scala.collection.mutable

class StructureCompletionPlugin extends ICompletionPlugin {

    override def id: String = StructureCompletionPlugin.ID

    override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages

    override def isApplicable(request:ICompletionRequest): Boolean = request.config.astProvider match {

        case Some(astProvider) =>
            if(languages.indexOf(astProvider.language)<0){
                false
            }
            else {
                request.actualYamlLocation match {
                    case Some(l) =>
                        if(l.inKey(request.position)){
                            true
                        }
                        else if(request.yamlLocation.get.hasSameValue(l)){
                            if(l.keyValue.isDefined) {
                                request.astNode.map(_.astUnit.positionsMapper) match {
                                    case Some(pm) => pm.point(request.position).line > l.keyValue.get.range.start.line
                                    case None => false
                                }
                            }
                            else {
                                false
                            }
                        }
                        else {
                            false
                        }
                    case _ => false
                }
            }
        case _ => false
    }

    override def suggest(request: ICompletionRequest): Seq[ISuggestion] = {

        if(!isApplicable(request)){
            Seq()
        }
        else {
            var result: Seq[ISuggestion] = Seq()
            request.astNode match {
                case Some(n) =>
                    if (n.isElement) {
                        var element = n.asElement.get
                        result = extractSuggestableProperties(element).map(_.nameId.get)
                            .map(pName => Suggestion(pName, id, pName, request.prefix))
                    }
                case _ =>
            }
            result
        }
    }

    def extractSuggestableProperties(node:IHighLevelNode):Seq[IProperty] = {

        var existingProperties:mutable.Map[String,IProperty] = mutable.Map()
        node.children.foreach(x=>x.property match {
            case Some(p) => p.nameId match {
                case Some(n) => existingProperties(n) = p
                case _ =>
            }
            case _ =>
        })

        var result = node.definition.allProperties
            .filter(p => p.nameId match {
            case Some(n) =>
                if(!existingProperties.contains(n)){
                    var e = p.getExtra(PropertySyntaxExtra).getOrElse(PropertySyntaxExtra.empty)

                    if(e.isKey || e.isValue || e.isHiddenFromUI){
                        false
                    }
                    else if(p.isMultiValue){
                        if(e.isEmbeddedInArray||e.isEmbeddedInMaps){
                            true
                        }
                        else {
                            false
                        }
                    }
                    else true
                }
                else false
            case _ => false
        })
        result
    }
}

object StructureCompletionPlugin {

    val ID = "structure.completion"

    val supportedLanguages:List[Vendor] = List(Raml10, Oas)

    def apply():StructureCompletionPlugin = new StructureCompletionPlugin()
}