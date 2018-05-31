package org.mulesoft.als.suggestions.plugins

import amf.core.remote.{Oas, Oas2, Oas2Yaml, Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.IHighLevelNode
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
        else {
            var prop = request.astNode.get.property
            if (request.actualYamlLocation.isEmpty) {
                false
            }
            else if (request.kind == LocationKind.KEY_COMPLETION
                && !prop.flatMap(_.range).exists(_.isArray)) {
                false
            }
            else if (request.actualYamlLocation.isEmpty || request.yamlLocation.isEmpty) {
                false
            }
            else {
                true
            }
        }
    }

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

        var prop:Option[IProperty] = None
        val astNode = request.astNode.get
        var parentNode = astNode.asElement
        if (astNode.isAttr) {
            prop = astNode.property
            parentNode = astNode.parent
        }
        else if(request.actualYamlLocation.get.hasSameValue(request.yamlLocation.get)) {
            if (astNode.isElement) {
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
        }
        else if(astNode.isElement){
            var definition = astNode.asElement.get.definition
            request.actualYamlLocation.flatMap(_.keyValue).map(_.yPart).foreach({
                case sc:YScalar =>
                    Option(sc.value).foreach(pName => {
                        prop = definition.property(pName.toString)
                    })
                case _ =>
            })

        }
        var result: ListBuffer[ISuggestion] = ListBuffer()
        prop.foreach(p => {
            var existing:ListBuffer[String] = ListBuffer()
            if(p.isMultiValue){
                parentNode.foreach(pn=>{
                    pn.attributes(p.nameId.get).filter(x=>x!=astNode).foreach(a=>{
                        a.value.foreach(av=>existing += av.toString)
                    })
                })
            }
            p.getExtra(PropertySyntaxExtra).foreach(extra => {
                extra.enum.filter(existing.indexOf(_)<0).foreach(x => {
                    var text = x.toString
                    result += Suggestion(text, id, text, request.prefix)
                })
            })
        })
        val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
        Promise.successful(response).future
    }
 }

object KnownPropertyValuesCompletionPlugin {
    val ID = "known.property.values.completion";
    
    val supportedLanguages:List[Vendor] = List(Raml10, Oas, Oas2, Oas2Yaml);

    def apply():KnownPropertyValuesCompletionPlugin = new KnownPropertyValuesCompletionPlugin();
}


