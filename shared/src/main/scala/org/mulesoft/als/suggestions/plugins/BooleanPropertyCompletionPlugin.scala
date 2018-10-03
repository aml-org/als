package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Oas20, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{YMap, YScalar, YSequence}

import scala.collection.mutable.{ListBuffer, Set}
import scala.concurrent.{Future, Promise}

class BooleanPropertyCompletionPlugin extends ICompletionPlugin {

    override def id: String = BooleanPropertyCompletionPlugin.ID

    override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages
    
    override def isApplicable(request: ICompletionRequest): Boolean = {

        if(request.config.astProvider.map(_.language).map(languages.indexOf).exists(_<0)){
            false
        }
        else if(request.astNode.isEmpty){
            false
        }
        else if(!request.astNode.exists(_.isAttr)) {
            false
        }
        else if(request.astNode.flatMap(_.property).isEmpty) {
            false
        }
        else {
            val prop = request.astNode.get.property.get
            if(prop.range.isEmpty){
                false
            }
            else {
                val range = prop.range.get
                range.isAssignableFrom("BooleanType") || range.isAssignableFrom("BooleanTypeDeclaration") || range.isAssignableFrom("boolean")
            }
        }
    }

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
        val result = Seq(Suggestion("true", id, "true", request.prefix), Suggestion("false", id, "false", request.prefix))
        val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
        Promise.successful(response).future
    }
 }

object BooleanPropertyCompletionPlugin {
    val ID = "boolean.property.completion";

    val supportedLanguages:List[Vendor] = List(Raml10, Oas, Oas20);

    def apply():BooleanPropertyCompletionPlugin = new BooleanPropertyCompletionPlugin();
}




