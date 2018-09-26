package org.mulesoft.als.suggestions.plugins.oas

import amf.core.model.domain.AmfScalar
import amf.core.remote.{Oas, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.builder.UniverseProvider
import org.mulesoft.high.level.interfaces.{IASTUnit, IHighLevelNode, IParseResult}
import org.mulesoft.typesystem.nominal_interfaces.IProperty
import org.mulesoft.als.suggestions.plugins.oas.DefinitionReferenceCompletionPlugin._
import org.mulesoft.high.level.implementation.SourceInfo
import org.mulesoft.positioning.{IPositionsMapper, YamlLocation, YamlPartWithRange}
import org.yaml.model.{DoubleQuoteMark, YScalar}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}

class DefinitionReferenceCompletionPlugin extends ICompletionPlugin {

    override def id: String = DefinitionReferenceCompletionPlugin.ID

    override def languages: Seq[Vendor] = DefinitionReferenceCompletionPlugin.supportedLanguages

    override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
        val result = determineRefCompletionCase(request) match {
            case Some(spv: SCHEMA_PROPERTY_VALUE) => extractRefs(request).map(x=>{
                var isJSON = request.config.astProvider.get.syntax == Syntax.JSON
                if(isJSON){
                    val text = "{ \"$ref\": \"" + x + "\" }"
                    List(text,text)
                }
                else {
                    var keyLine = request.yamlLocation.flatMap(_.keyNode)
                        .map(_.range.start.line).getOrElse(-1)
                    var line = request.astNode.get.astUnit.positionsMapper.point(request.position).line
                    var text = "\"$ref\": \"" + x + "\""
                    val display = "$ref: " + x
                    if(line == keyLine){
                        text = "\n" + " " * spv.refPropOffset + text
                    }
                    List(text,display)
                }
            }).map(x=>Suggestion(x.head,x(1),x(1),request.prefix));
            
            case Some(rpv: REF_PROPERTY_VALUE) => extractRefs(request).map(_uri=>{
                var uri = _uri
                val text = if(request.prefix.startsWith("/") && uri.startsWith("#")){
                    uri = uri.substring(1)
                    uri
                }
                else {
                    val needQuotes = request.actualYamlLocation.flatMap(_.value).map(_.yPart) match {
                        case Some(l) => l match {
                            case sc: YScalar => {
                                sc.mark != DoubleQuoteMark
                            }
                            case _ => false
                        }
                        case _ => false
                    }
                    if (needQuotes) {
                        "\"" + uri + "\""
                    }
                    else {
                        uri
                    }
                }
                Suggestion(text,uri,uri,request.prefix)
            });
            
            case _ => Seq();
        }

        val response = CompletionResponse(result, LocationKind.VALUE_COMPLETION, request)
        Promise.successful(response).future
    }
    
    def extractRefs(request: ICompletionRequest): Seq[String] = {
        val elementOpt = request.astNode.flatMap(x => {
            if (x.isElement) {
                x.asElement
            }
            else if (x.isAttr) {
                x.parent
            }
            else {
                None
            }
        })
        var propNameOpt = elementOpt.flatMap(_.property).flatMap(_.nameId)
        var nameOpt:Option[String] = None
        if(propNameOpt.contains("definitions")){
            nameOpt = elementOpt.flatMap(_.attribute("name")).flatMap(_.value).map(_.toString)
        }
        request.astNode.get.astUnit.rootNode.elements("definitions").map(x => x.attribute("name").get.value.get).filter(y => !nameOpt.contains(y)).map(n=>s"#/definitions/$n");
    }

    override def isApplicable(request: ICompletionRequest): Boolean = {
        determineRefCompletionCase(request).nonEmpty
    }

    private def determineRefCompletionCase(request:ICompletionRequest):Option[RefCompletionCase] = {

        checkPropertyDetectedCase(request) match {
            case Some(x) => Some(x)
            case None => checkPropertyNotDetectedCase(request) match {
                case Some(x) => Some(x)
                case None => None
            }
        }

    }

    private def checkPropertyNotDetectedCase(request:ICompletionRequest):Option[RefCompletionCase] = {
        if(request.astNode.isEmpty || !request.astNode.get.isElement){
            None
        }
        else if(request.actualYamlLocation.isEmpty
            ||request.actualYamlLocation.get.isEmpty
            ||request.actualYamlLocation.get.keyValue.isEmpty){
            None
        }
        else {
            var node: IHighLevelNode = request.astNode.get.asElement.get
            var definition = node.definition
            var defName = definition.nameId.get
            var position: Int = request.position
            var actualLocation = request.actualYamlLocation.get
            var pm = node.astUnit.positionsMapper

            acceptedProperties.get.find(_.domain.exists(x=>definition.isAssignableFrom(x.nameId.get))) match {
                case Some(p) =>
                    val keyValue = actualLocation.keyValue.get
                    keyValue.yPart match {
                        case scalar: YScalar =>
                            val key = scalar.value
                            if (p.nameId.contains(key)) {
                                selectPreciseCompletionStyle(position, actualLocation, pm,  node.astUnit)
                            }
                            else if(key == "$ref"){
                                Some(REF_PROPERTY_VALUE())
                            }
                            else {
                                None
                            }
                        case _ => None
                    }
                case None => None
            }
        }
    }

    private def selectPreciseCompletionStyle(position: Int, actualLocation: YamlLocation, pm: IPositionsMapper, astUnit: IASTUnit):Option[RefCompletionCase] = {
        var keyValue = actualLocation.keyValue.get
        val keyStart = keyValue.range.start
        val posPoint = pm.point(position)
        if (keyStart.line == posPoint.line) {
            var si = SourceInfo().withSources(List(actualLocation.mapEntry.get.yPart))
            si.init(astUnit.project,Some(astUnit))
            var valOffset = si.valueOffset.get
            Some(SCHEMA_PROPERTY_VALUE(valOffset))
        }
//        else if (posPoint.line > keyStart.line) {
//            if (actualLocation.value.isDefined) {
//                var si = SourceInfo().withSources(List(actualLocation.mapEntry.get.yPart))
//                si.init(astUnit.project,Some(astUnit))
//                var valOffset = si.valueOffset
//                if (valOffset.contains(posPoint.column)) {
//                    Some(REF_PROPERTY_VALUE())
//                }
//                else {
//                    None
//                }
//
//            }
//            else {
//                var valOffset = keyStart.column + YAML_OFFSET
//                if (valOffset == posPoint.column) {
//                    Some(REF_PROPERTY_VALUE())
//                }
//                else {
//                    None
//                }
//            }
//        }
        else {
            None
        }
    }

    private def checkPropertyDetectedCase(request:ICompletionRequest):Option[RefCompletionCase] = {
        if (request.astNode.isEmpty) {
            None
        }
        else if (request.actualYamlLocation.isEmpty
            || request.actualYamlLocation.get.isEmpty
            || request.actualYamlLocation.get.keyNode.isEmpty) {
            None
        }
        else if(request.yamlLocation.get.value.get.yPart!=request.actualYamlLocation.get.value.get.yPart){
            None
        }
        else {
            var node: IParseResult = request.astNode.get
            var position: Int = request.position
            var actualLocation = request.actualYamlLocation.get
            var pm = node.astUnit.positionsMapper

            node.property match {
                case Some(nodeProp) => acceptedProperties.get.find(x =>
                    x.nameId.isDefined && x.nameId == nodeProp.nameId) match {
                    case Some(p) =>
                        if (actualLocation.inKey(position)) {
                            None
                        }
                        else if(actualLocation.keyValue.isDefined){
                            selectPreciseCompletionStyle (position, actualLocation, pm,node.astUnit)
                        }
                        else {
                            None
                        }
                    case None => if(nodeProp.nameId.contains("$ref")){
                        if(node.parent.map(_.definition).exists(_.isAssignableFrom("SchemaObject"))){
                            Some(REF_PROPERTY_VALUE())
                        }
                        else {
                            None
                        }
                    }
                    else {
                        None
                    }
                }
                case None => None
            }
        }
    }

}


object DefinitionReferenceCompletionPlugin {

    private val YAML_OFFSET = 2

    val ID = "definition.reference.completion.plugin"

    val supportedLanguages = List(Oas)

    private var acceptedProperties: Option[Seq[IProperty]] = None

    def apply():DefinitionReferenceCompletionPlugin = {
        acceptedProperties match {
            case Some(x) =>
            case None => init()
        }
        new DefinitionReferenceCompletionPlugin()
    }

    private def init():Unit = {

        UniverseProvider.universe(Oas) match {
            case Some(u) =>
                var props:ArrayBuffer[IProperty] = ArrayBuffer()
                acceptedProperties = Some(props)
                u.`type`("SchemaObject").foreach(t => {
                    t.property("properties").foreach(p => props += p)
                    t.property("items").foreach(p => props += p)
                    t.property("additionalProperties").foreach(p => props += p)
                    t.property("allOf").foreach(p => props += p)
                })

                u.`type`("SwaggerObject").flatMap(_.property("definitions")).foreach(p => props += p)
                u.`type`("BodyParameterObject").flatMap(_.property("schema")).foreach(p => props += p)
                u.`type`("ResponseObject").flatMap(_.property("schema")).foreach(p => props += p)

            case None => acceptedProperties = Some(Seq())
        }
    }



}

private sealed class RefCompletionCase {}

private sealed class REF_PROPERTY_VALUE() extends RefCompletionCase{}

private object REF_PROPERTY_VALUE{
    def apply():REF_PROPERTY_VALUE = new REF_PROPERTY_VALUE()
}

private sealed class SCHEMA_PROPERTY_VALUE(val refPropOffset: Int) extends RefCompletionCase{}

private object SCHEMA_PROPERTY_VALUE{
    def apply(refPropOffset:Int):SCHEMA_PROPERTY_VALUE = new SCHEMA_PROPERTY_VALUE(refPropOffset)
}