package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.Suggestion
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.typesystem.nominal_interfaces.extras.PropertySyntaxExtra
import org.yaml.model.{YMap, YScalar}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}

class KnownPropertyValuesCompletionPlugin extends ICompletionPlugin {

    override def id: String = KnownPropertyValuesCompletionPlugin.ID

    override def languages: Seq[Vendor] = StructureCompletionPlugin.supportedLanguages
    
    override def isApplicable(request: ICompletionRequest): Boolean = {
        if(request.astNode.isEmpty){
            false
        }
        else if(request.actualYamlLocation.isEmpty){
            false
        }
        else if (request.kind == LocationKind.KEY_COMPLETION){
            false
        }
        else if(request.actualYamlLocation.isEmpty || request.yamlLocation.isEmpty){
            false
        }
        else {
            request.actualYamlLocation.get.hasSameValue(request.yamlLocation.get)
        }
    }

    override def suggest(request: ICompletionRequest): Future[Seq[ISuggestion]] = {

        val astNode = request.astNode.get
        var prop:Option[IProperty] = None
        if(astNode.isAttr){
            prop = astNode.property
        }
        else if(astNode.isElement) {
            var valueLocation = request.yamlLocation.get.value.get
            var propName: Option[String] = None
            valueLocation.yPart match {
                case yMap: YMap =>
                    yMap.entries.find(e => YamlLocation(e, astNode.astUnit.positionsMapper).mapEntry.get.containsPosition(request.position)).foreach(e => e.key.value match {
                        case sc: YScalar => propName = Some(sc.value.toString)
                        case _ =>
                    })
                case _ =>
            }
            prop = propName.flatMap(pName => astNode.asElement.get.definition.property(pName))
        }
        var result: ListBuffer[ISuggestion] = ListBuffer()
        prop.foreach(p => {
            p.getExtra(PropertySyntaxExtra).foreach(extra => {
                extra.enum.foreach(x => {
                    var text = x.toString
                    result += Suggestion(text, id, text, request.prefix)
                })
            })
        })
        Promise.successful(result).future
    }
 }

object KnownPropertyValuesCompletionPlugin {
    val ID = "known.property.values.completion";

    val supportedLanguages:List[Vendor] = List(Raml10, Oas);

    def apply():KnownPropertyValuesCompletionPlugin = new KnownPropertyValuesCompletionPlugin();
}


