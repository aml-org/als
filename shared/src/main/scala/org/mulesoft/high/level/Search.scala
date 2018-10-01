package org.mulesoft.high.level

import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.AmfScalar
import amf.core.remote._
import org.mulesoft.high.level.Search.collectOASReferences
import org.mulesoft.high.level.interfaces.{IASTUnit, IHighLevelNode, IParseResult}
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.mulesoft.typesystem.project.ModuleDependencyEntry
import org.mulesoft.typesystem.syaml.to.json.YJSONWrapper
import org.yaml.model.{YMap, YScalar}

import scala.collection.mutable.ListBuffer



object Search {
    // $COVERAGE-OFF$
    def getDeclarations(unit:IASTUnit,typeName:String):Seq[Declaration] = {

        var result:ListBuffer[Declaration] = ListBuffer()
        var ownDeclarations: Seq[IHighLevelNode] = extractDeclarations(unit, typeName)
        ownDeclarations.foreach(decl=>result += new Declaration(decl,unit,None))
        var libsToSearch:Iterable[ModuleDependencyEntry[IASTUnit]]
                = unit.dependencies.values.flatMap(x =>
            if (x.isModule) Some(x.asInstanceOf[ModuleDependencyEntry[IASTUnit]]) else None)

        libsToSearch.foreach(libDependency=>{
            val libUnit = libDependency.tc
            var libDeclarations = extractDeclarations(libUnit,typeName)
            libDeclarations.foreach(decl=>result += new Declaration(decl,libUnit,Option(libDependency.namespace)))
        })
        result
    }

    private def extractDeclarations(unit: IASTUnit, typeName: String) = {
        var ownDeclarations: Seq[IHighLevelNode] = unit.rootNode.children.flatMap(x => {
            if (!x.isElement) {
                None
            }
            else if (x.asElement.get.definition.isAssignableFrom(typeName)) {
                x.asElement
            }
            else {
                None
            }
        })
        ownDeclarations
    }
    // $COVERAGE-ON$
    def getNodesOfType(
           unit: IASTUnit,
           typeName: String,
           filter:IHighLevelNode => Boolean = n=>true,
           considerDependencies:Boolean = true):Seq[IHighLevelNode] = {

        var units:ListBuffer[IASTUnit] = ListBuffer(unit)
        if(considerDependencies){
            units ++= unit.dependencies.values.map(_.tc)
        }
        var result:ListBuffer[IHighLevelNode] = ListBuffer()
        units.foreach(u=>extractNodesOfType(u.rootNode,typeName,result,filter))
        result
    }

    def extractNodesOfType(
           node: IHighLevelNode,
           typeName: String,
           result: ListBuffer[IHighLevelNode],
           filter:IHighLevelNode => Boolean = n=>true):Unit = {
        if(node.definition.isAssignableFrom(typeName) && filter(node)){
            result += node
        }
        node.children.filter(_.isElement).foreach(ch=>extractNodesOfType(ch.asElement.get,typeName,result,filter))
    }

    def getTemplateReferences(templateNode:IHighLevelNode,unit:IASTUnit):Seq[IHighLevelNode] = {
        var templateRefTypeName:Option[String] = None
        var definition = templateNode.definition
        var targetName:Option[String] = None
        var refTypeName:Option[String] = None
        if(definition.nameId.contains("ResourceType")){
            targetName = Some("resourceType")
            refTypeName = Some("ResourceTypeRef")
        }
        else if(definition.nameId.contains("Trait")){
            targetName = Some("trait")
            refTypeName = Some("TraitRef")
        }
        if(targetName.isDefined && refTypeName.isDefined) {
            var id = templateNode.amfNode.id
            var filter: IHighLevelNode => Boolean = (node: IHighLevelNode) => {
                node.element(targetName.get) match {
                    case Some(x) => x.amfNode.fields.getValue(LinkableElementModel.TargetId).value match {
                        case sc: AmfScalar => sc.value == id
                        case _ => false
                    }
                    case _ => false
                }
            }
            getNodesOfType(unit,refTypeName.get,filter)
        }
        else {
            Seq()
        }
    }

    def findReferences(node:IParseResult,position:Int):Option[ReferenceSearchResult] = {
        val pm = node.astUnit.positionsMapper
        var locOpt = node.sourceInfo.yamlSources.headOption.map(YamlLocation(_, pm))
        var language = node.astUnit.project.language
        if(locOpt.isEmpty){
            None
        }
        else {
            val prop = node.property
            if (node.isElement) {
                var eNode = node.asElement.get
                if (!locOpt.get.inKey(position)) {
                    None
                }
                else if (language == Raml || language == Raml10) {
                    if (eNode.definition.nameId.contains("ResourceType") || eNode.definition.nameId.contains("Trait")) {
                        var refs = getTemplateReferences(eNode, node.astUnit.project.rootASTUnit)
                        Some(ReferenceSearchResult(eNode, refs))
                    }
                    else if (eNode.definition.isAssignableFrom("TypeDeclaration")) {
                        val refs = typeReferences(eNode)
                        Some(ReferenceSearchResult(eNode, refs))
                    }
                    else {
                        None
                    }
                }
                else if (language == Oas || language == Oas20) {
                    if (prop.isEmpty) {
                        None
                    }
                    else {
                        var targetDef: Option[String] = None
                        var keyAttrName: Option[String] = Some("key")
                        if(eNode.definition.isAssignableFrom("ParameterDefinitionObject")){
                            targetDef = Some("ParameterObject")
                        }
                        else {
                            eNode.definition.nameId match {
                                case Some("DefinitionObject") =>
                                    targetDef = Some("SchemaObject")
                                    keyAttrName = Some("name")
                                case Some("ResponseDefinitionObject") => targetDef = Some("ResponseObject")
                                case _ =>
                            }
                        }
                        var refOpt = eNode.attribute(keyAttrName.get).flatMap(_.value).map(v => s"#/${prop.get.nameId.get}/${v.toString}")
                        if (targetDef.isEmpty || refOpt.isEmpty) {
                            None
                        }
                        else {
                            var refs = collectOASReferences(refOpt.get, targetDef.get, eNode.astUnit)
                            Some(ReferenceSearchResult(eNode, refs))
                        }
                    }
                }
                else {
                    None
                }
            }
            else if (node.isAttr) {
                var lattr = node.asAttr.get
                val parentOpt = node.parent
                if (parentOpt.isDefined) {
                    var pDef = parentOpt.get.definition
                    if (language == Raml || language == Raml10) {
                        if (pDef.isAssignableFrom("TypeDeclaration")
                            && prop.flatMap(_.nameId).contains("name")) {

                            val parent = parentOpt.get
                            val refs = typeReferences(parent)
                            Some(ReferenceSearchResult(parent, refs))
                        }
                        else {
                            None
                        }
                    }
                    else if (language == Oas || language == Oas20) {
                        var targetDef: Option[String] = None
                        var keyAttrName: Option[String] = None
                        if(pDef.isAssignableFrom("DefinitionObject")){
                            targetDef = Some("SchemaObject")
                            keyAttrName = Some("name")
                        }
                        else if(pDef.isAssignableFrom("ParameterDefinitionObject")){
                            targetDef = Some("ParameterObject")
                            keyAttrName = Some("key")
                        }
                        else if(pDef.isAssignableFrom("ResponseDefinitionObject")){
                            targetDef = Some("ResponseObject")
                            keyAttrName = Some("key")
                        }
                        if(targetDef.isDefined
                            && keyAttrName.contains(prop.get.nameId.get)
                            && parentOpt.get.property.isDefined){
                            var parentProp = parentOpt.get.property.get.nameId.get
                            var refOpt = lattr.value.map(v=>s"#/$parentProp/${v.toString}")
                            if(refOpt.isDefined){
                                var refs = collectOASReferences(refOpt.get, targetDef.get, node.astUnit)
                                Some(ReferenceSearchResult(parentOpt.get, refs))
                            }
                            else{
                                None
                            }
                        }
                        else {
                            None
                        }
                    }
                    else {
                        None
                    }
                }
                else {
                    None
                }
            }
            else {
                None
            }
        }

    }

    def findReferencesByPosition(unit:IASTUnit,position:Int):Option[ReferenceSearchResult] = unit.rootNode.getNodeByPosition(position).flatMap(findReferences(_,position))

    def findDefinition(_node:IParseResult,position:Int):Option[ReferenceSearchResult] = {
        var language = _node.astUnit.project.language
        val pm = _node.astUnit.positionsMapper
        var yLoc = _node.sourceInfo.yamlSources.headOption.map(YamlLocation(_, pm))
        if (_node.isElement) {
            var node = _node.asElement.get
            if(language == Raml || language == Raml10) {
                if (yLoc.nonEmpty && yLoc.get.value.nonEmpty) {
                    yLoc.get.value.get.yPart match {
                        case map: YMap =>
                            if (map.entries.nonEmpty) {
                                yLoc = Some(YamlLocation(map.entries.head.key, pm))
                            }
                        case _ =>
                    }
                }
                if (yLoc.isEmpty) {
                    None
                }
                else if (!yLoc.get.inValue(position)) {
                    None
                }
                else {
                    var refNode: Option[IHighLevelNode] = None
                    var defNode: Option[IHighLevelNode] = None
                    var definition = node.definition
                    var targetName: Option[String] = None
                    var typeName: Option[String] = None
                    if (definition.nameId.contains("ResourceTypeRef")) {
                        targetName = Some("resourceType")
                        typeName = Some("ResourceType")
                    }
                    else if (definition.nameId.contains("TraitRef")) {
                        targetName = Some("trait")
                        typeName = Some("Trait")
                    }
                    if (targetName.isDefined && typeName.isDefined) {
                        refNode = Some(node)
                        defNode = yLoc.get.value.get.yPart match {
                            case sc: YScalar =>
                                var name = sc.value.toString
                                findDefinitionByName(refNode.get.astUnit, typeName.get, name)
                            case _ => None
                        }
                    }
                    if (refNode.isDefined && defNode.isDefined) {
                        Option(ReferenceSearchResult(defNode.get, List(refNode.get)))
                    }
                    else {
                        None
                    }
                }
            }
            else if(language == Oas || language == Oas20){
                 oasReference(node,position).flatMap(oasDeclarationByReference(node.astUnit,_)).filter(_.definition.isAssignableFrom(node.definition.nameId.get)).map(x=>ReferenceSearchResult(x, List(node)))
            }
            else {
                None
            }
        }
        else if(_node.isAttr){
            var attr = _node.asAttr.get
            val parent = attr.parent.get
            if(language == Raml || language == Raml10) {
                attr.parent match {
                    case Some(p) =>
                        if (p.definition.isAssignableFrom("TypeDeclaration")
                            && attr.property.isDefined
                            && attr.property.get.nameId.contains("type")) {

                            typeDeclarationByPosition(attr, position) match {
                                case Some(decl) => Option(ReferenceSearchResult(decl, List(parent)))
                                case _ => None
                            }
                        }
                        else {
                            None
                        }
                    case _ => None
                }
            }
            else if(language == Oas || language == Oas20){
                if(yLoc.nonEmpty && yLoc.get.inValue(position)) {
                    if (attr.property.get.nameId.contains("$ref")) {
                        var superCalssName = parent.definition.nameId.get
                        if(parent.definition.isAssignableFrom("ResponseObject")){
                            superCalssName = "ResponseObject"
                        }
                        attr.value.map(_.toString).flatMap(oasDeclarationByReference(attr.astUnit, _)).filter(_.definition.isAssignableFrom(superCalssName)).map(x => ReferenceSearchResult(x, List(parent)))
                    }
                    else {
                        None
                    }
                }
                else{
                    None
                }
            }
            else {
                None
            }
        }
        else {
            None
        }
    }

    def findDefinitionByPosition(unit:IASTUnit,position:Int):Option[ReferenceSearchResult] = unit.rootNode.getNodeByPosition(position).flatMap(findDefinition(_,position))

    def findDefinitionByName(unit:IASTUnit,typeName:String,name:String, nameAttr:String="name"):Option[IHighLevelNode] = {
        var result:Option[IHighLevelNode] = unit.rootNode.children.find(ch=>{
            if(!ch.isElement){
                false
            }
            else if(!ch.asElement.get.definition.isAssignableFrom(typeName)){
                false
            }
            else{
                ch.asElement.get.attribute(nameAttr) match {
                    case Some(a) => a.value.contains(name)
                    case _ => false
                }
            }
        }).flatMap(_.asElement)
        if(result.isEmpty) {
            var deps: Iterable[ModuleDependencyEntry[IASTUnit]] = unit.dependencies.values.flatMap({
                case me: ModuleDependencyEntry[IASTUnit] => Some(me)
                case _ => None
            })
            var ind = 0
            while (result.isEmpty && ind < name.length) {
                if (name.charAt(ind) == '.') {
                    var namespace = name.substring(0, ind)
                    var name1 = name.substring(ind + 1)
                    deps.find(_.namespace == namespace).foreach(x => {
                        val t = findDefinitionByName(x.tc, typeName, name1, nameAttr)
                        result = t
                    })
                }
                ind += 1
            }
        }
        result
    }

    def extractTypeName(text:String,position:Int):Option[String] = {
        if(text.isEmpty || position < 0 || position > text.length){
            None
        }
        else {
            var start = position
            if(text.lengthCompare(start)==0){
                start -= 1
            }
            if(start > 0 && ("" + text.charAt(start)).replaceAll("[\\[\\]|]","").isEmpty){
                start -= 1
            }
            while (start>0 && ("" + text.charAt(start-1)).replaceAll("[^\\[\\]|]","").isEmpty){
                start -= 1
            }
            var end = position
            while (end < text.length && ("" + text.charAt(end)).replaceAll("[^\\[\\]|]","").isEmpty){
                end += 1
            }
            val result = text.substring(start, end)
            Some(result)
        }
    }

    def typeDeclarationByPosition(_typeAttr:IParseResult, position:Int):Option[IHighLevelNode] = {
        if(!_typeAttr.isAttr){
            None
        }
        else {
            var typeAttr = _typeAttr.asAttr.get
            typeAttr.value match {
                case Some(v) => v match {
                    case w:JSONWrapper => w.kind match {
                        case STRING =>
                            var sources = typeAttr.sourceInfo.yamlSources
                            if (sources.isEmpty) {
                                None
                            }
                            else {
                                var pm = typeAttr.astUnit.positionsMapper
                                var loc = YamlLocation(sources.head, pm)
                                loc.value match {
                                    case Some(yv) => yv.yPart match {
                                        case sc: YScalar =>
                                            var off = position - yv.range.start.position
                                            var text = sc.value.toString
                                            extractTypeName(text, off) match {
                                                case Some(name) => findDefinitionByName(typeAttr.astUnit, "TypeDeclaration", name)
                                                case _ => None
                                            }

                                        case _ => None
                                    }
                                    case _ => None
                                }
                            }
                        case _ => None
                    }
                    case _ => None
                }

                case None => None
            }
        }
    }

    def typeReferences(typeNode:IHighLevelNode):Seq[IHighLevelNode] = {
        var typeNameOpt:Option[String] = None
        typeNode.attribute("name") match {
            case Some(a) => a.value match {
                case Some(aVal) => aVal match {
                    case str:String => typeNameOpt = Some(str)
                    case _ =>
                }
                case _ =>
            }
            case _ =>
        }
        if(typeNameOpt.nonEmpty) {
            var typeName = typeNameOpt.get
            val nodeUnit = typeNode.astUnit
            var result: ListBuffer[IHighLevelNode] = ListBuffer()
            result ++= typeReferencesInUnit(nodeUnit, typeName)

            nodeUnit.dependants.values.flatMap({
                case me: ModuleDependencyEntry[IASTUnit] => Some(me)
                case _ => None
            }).foreach(me=>{
                var tn = me.namespace + "." + typeName
                result ++= typeReferencesInUnit(me.tc, tn)
            })
            result
        }
        else {
            Seq()
        }
    }

    def typeReferencesInUnit(unit:IASTUnit,name:String):Seq[IHighLevelNode] = {
        var filter:IHighLevelNode=>Boolean = node =>{
            node.attribute("type") match {
                case Some(a) => a.value match {
                    case Some(v) => v match {
                        case w:JSONWrapper => w.kind match {
                            case STRING => w.value(STRING).get.contains(name)
                            case _ => false
                        }
                        case str: String => str.contains(name)
                        case _ => false
                    }
                    case _ => false
                }
                case _ =>false
            }
        }
        getNodesOfType(unit,"TypeDeclaration",filter,false)
    }

    def oasReference(node:IParseResult,position:Int = -1):Option[String] = {
//        val pm = node.astUnit.positionsMapper
//        node.sourceInfo.yamlSources.headOption.flatMap(YamlLocation(_,pm).value).flatMap(x=> x.yPart match {
//            case map:YMap => map.entries.find(e=>e.key.value match {
//                case sc:YScalar => sc.value == "$ref"
//                case _ => false
//            }).map(YamlLocation(_,pm))
//            case _ => None
//        }).filter(x => position < 0 || x.inValue(position)).flatMap(_.value).map(_.yPart).flatMap({
//            case sc:YScalar => Option(sc.value.toString)
//            case _ => None
//        })
        node.asElement.flatMap(_.attribute("$ref")).flatMap(a =>
            a.sourceInfo.yamlSources.headOption match {
                case Some(yPart) =>
                    if(position < 0 || YamlLocation(yPart,node.astUnit.positionsMapper).inValue(position)) {
                        a.value
                    }
                    else {
                        None
                    }
                case _ => None
            }).map(_.toString)
    }

    def oasDeclarationByReference(unit:IASTUnit,reference:String):Option[IHighLevelNode] = {
        if(reference.isEmpty){
            None
        }
        else {
            var arr: Array[String] = reference.split("/").filter(x=>x.nonEmpty&&x!="#")
            if(arr.length==2){
                var propName = arr(0)
                var key = arr(1)
                unit.rootNode.elements(propName).find(x=>{
                    var keyAttrName = "key"
                    if(x.definition.nameId.contains("DefinitionObject")){
                        keyAttrName = "name"
                    }
                    x.attribute(keyAttrName).flatMap(_.value).map({
                        case str:String => str == key
                        case _ => false
                    }).orElse(Some(false)).get
                })
            }
            else {
                None
            }
        }
    }

    def collectOASReferences(ref:String,typeName:String,unit:IASTUnit):Seq[IHighLevelNode] = {

        var filter: IHighLevelNode => Boolean = n => {
            oasReference(n).contains(ref)
        }
        getNodesOfType(unit,typeName,filter)
    }

}

class Declaration(val node:IHighLevelNode, val declaringUnit:IASTUnit, val namespace:Option[String]) {}

class ReferenceSearchResult(val definition:IHighLevelNode, val references:Seq[IHighLevelNode]) {
}

object ReferenceSearchResult {
    def apply(definition: IHighLevelNode, references: Seq[IHighLevelNode]):
    ReferenceSearchResult = new ReferenceSearchResult(definition, references)
}