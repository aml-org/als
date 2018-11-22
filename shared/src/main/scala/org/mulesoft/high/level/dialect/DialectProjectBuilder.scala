package org.mulesoft.high.level.dialect

import amf.core.remote.Vendor._
import amf.core.annotations.SourceAST
import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.metamodel.document.DialectInstanceModel
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstance, DialectInstanceFragment, DialectInstanceLibrary}
import amf.plugins.document.vocabularies.model.domain.{DialectDomainElement, PropertyMapping}
import org.mulesoft.high.level.implementation._
import org.mulesoft.high.level.interfaces.{IFSProvider, IHighLevelNode, IProject}
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.dialects.extras.SourcePropertyMapping
import org.mulesoft.typesystem.dialects.{BuiltinUniverse, DialectUniverse}
import org.mulesoft.typesystem.nominal_interfaces.IDialectUniverse
import org.mulesoft.typesystem.project.{TypeCollection, TypeCollectionBundle}
import org.yaml.model.YPart

import scala.collection.mutable

class DialectProjectBuilder {

    def buildProject(rootUnit: DialectInstanceLibrary, fsResolver: IFSProvider): IProject = {
        val u: IDialectUniverse = getUniverse(rootUnit.fields(DialectInstanceModel.DefinedBy).toString)
        val unitPath = TypeBuilder.normalizedPath(rootUnit)
        val typeCollection = new TypeCollection(unitPath,u.asInstanceOf[DialectUniverse],null)
        val bundle = new TypeCollectionBundle()
        bundle.registerTypeCollection(typeCollection)

        val project = new Project(bundle, AML, fsResolver)
        val unit = new ASTUnit(rootUnit, typeCollection, project)
        project.setRootUnit(unit)
        if(u.library.isEmpty){
            throw new Error(s"No library type is registered for dialect: ${rootUnit.definedBy().value()}")
        }
        val rootType = u.library.get
        val rootNode = new ASTNodeImpl(rootUnit, rootUnit, None, rootType, None)
        rootNode.setASTUnit(unit)

        unit.setRootNode(rootNode)

        rootUnit.declares.foreach(de=>{
            val dde = de.asInstanceOf[DialectDomainElement]
            declarationPropertyNameFromId(dde.id).foreach(propName=>{
                val srcOpt = dde.annotations.find(classOf[SourceAST]).map(_.ast)
                addObjectProperty(rootNode, dde, propName, srcOpt)
            })
        })

        rootNode.initSources(Some(unit), None)
        project
    }

    def buildProject(rootUnit: DialectInstanceFragment, fsResolver: IFSProvider): IProject = {
        val u: IDialectUniverse = getUniverse(rootUnit.definedBy())
        val unitPath = TypeBuilder.normalizedPath(rootUnit)
        val typeCollection = new TypeCollection(unitPath,u.asInstanceOf[DialectUniverse],null)
        val bundle = new TypeCollectionBundle()
        bundle.registerTypeCollection(typeCollection)

        val project = new Project(bundle, AML, fsResolver)
        val unit = new ASTUnit(rootUnit, typeCollection, project)
        project.setRootUnit(unit)

        val fragmentTypeNameOpt = fragmentType(unit.text)
        if(fragmentTypeNameOpt.isEmpty){
            throw new RuntimeException("Unable to detect fragment type")
        }

        val rootTypeOpt = u.fragments.get(fragmentTypeNameOpt.get)
        if(rootTypeOpt.isEmpty){
            throw new RuntimeException(s"Unable to detect type for fragment name: ${fragmentTypeNameOpt.get}")
        }
        val rootNode = new ASTNodeImpl(rootUnit.encodes, rootUnit, None, rootTypeOpt.get, None)
        rootNode.setASTUnit(unit)

        unit.setRootNode(rootNode)
        Option(rootUnit.encodes).foreach(enc => {
            val dde = enc.asInstanceOf[DialectDomainElement]
            fillProperties(dde, rootNode, None)
        })

        rootNode.initSources(Some(unit), None)
        project
    }

    def fragmentType(content:String):Option[String] = {
        val ind1 = content.indexOf("#%")
        val ind2 = content.indexOf("/")
        if(ind1 < 0 || ind2 < 0 || ind1 > ind2){
            None
        }
        else {
            val tName = content.substring(ind1 + "#%".length, ind2).trim
            Some(tName)
        }
    }

    def getUniverse(dialectId:String):IDialectUniverse = {
        val dialectOpt = AMLPlugin.registry.allDialects().find(_.id == dialectId)
        val universe: IDialectUniverse = dialectOpt match {
            case Some(d) => DialectUniversesProvider.getUniverse(d)
            case _ => new DialectUniverse(dialectId, None, "")
        }
        universe
    }

    def buildProject(rootUnit: DialectInstance, fsResolver: IFSProvider): IProject = {

        val u: IDialectUniverse = getUniverse(rootUnit.definedBy().value())
        val unitPath = TypeBuilder.normalizedPath(rootUnit)
        val typeCollection = new TypeCollection(unitPath,u.asInstanceOf[DialectUniverse],null)
        val bundle = new TypeCollectionBundle()
        bundle.registerTypeCollection(typeCollection)

        val project = new Project(bundle, AML, fsResolver)
        val unit = new ASTUnit(rootUnit, typeCollection, project)
        project.setRootUnit(unit)

        if(u.root.isEmpty){
            throw new Error(s"No root type is registered for dialect: ${rootUnit.definedBy().value()}")
        }

        val rootType = u.root.get
        val rootNode = new ASTNodeImpl(rootUnit.encodes, rootUnit, None, rootType, None)
        rootNode.setASTUnit(unit)

        unit.setRootNode(rootNode)
        Option(rootUnit.encodes).foreach(enc => {
            val dde = enc.asInstanceOf[DialectDomainElement]
            fillProperties(dde, rootNode, None)
        })

        rootUnit.declares.foreach(de=>{
            val dde = de.asInstanceOf[DialectDomainElement]
            declarationPropertyNameFromId(dde.id).foreach(propName=>{
                val srcOpt = dde.annotations.find(classOf[SourceAST]).map(_.ast)
                addObjectProperty(rootNode, dde, propName, srcOpt)
            })
        })

        rootNode.initSources(Some(unit), None)
        project
    }

    private def fillProperties(_dde: DialectDomainElement, parent: ASTNodeImpl, pm:Option[PropertyMapping]):Unit = {
        var dde = _dde
        val unit = parent.astUnit
        val rootUnit = unit.baseUnit
        val sourcesMap: mutable.Map[String, SourceAST] = mutable.Map()
        dde.propertyAnnotations.map(e => (e._1, e._2.find(classOf[SourceAST]))).foreach(e => e._2.foreach(src => sourcesMap.put(e._1, src)))

        val definition = parent.definition
        dde.literalProperties.foreach(p => {
            val propId = p._1
            val propValue = p._2
            propertyNameFromId(propId).foreach(propName => {
                val srcOpt = sourcesMap.get(propId).map(_.ast)
                val sourceInfo = SourceInfo().withReferingUnit(unit)
                srcOpt.foreach(x => sourceInfo.withSources(Seq(x)))

                val buf = new LiteralPropertyValueBuffer(dde, propId, propValue, srcOpt)

                val prop = new ASTPropImpl(dde, rootUnit, Some(parent), definition, definition.property(propName), buf)
                prop.setASTUnit(unit)
                parent.addChild(prop)
            })
        })
        dde.objectProperties.foreach(p => {
            val propId = p._1
            val childNode = p._2
            propertyNameFromId(propId).foreach(propName => {
                val srcOpt = sourcesMap.get(propId).map(_.ast)
                addObjectProperty(parent, childNode, propName, srcOpt)
            })
        })
        dde.objectCollectionProperties.foreach(p => {
            val propId = p._1
            val values:Seq[DialectDomainElement] = p._2
            propertyNameFromId(propId).foreach(propName => {
                values.foreach(x=>{
                    addObjectProperty(parent, x, propName, None)
                })
            })
        })
        dde.mapKeyProperties.foreach(p => {
            val propId = p._1
            val propValue = p._2
            DialectUniverseBuilder.findPropertyByRdfId(propId,definition).foreach(p => {
                val srcOpt = sourcesMap.get(propId).map(_.ast)
                val sourceInfo = SourceInfo().withReferingUnit(unit)
                srcOpt.foreach(x => sourceInfo.withSources(Seq(x)))

                val buf = new LiteralPropertyValueBuffer(dde, propId, propValue, srcOpt)

                val prop = new ASTPropImpl(dde, rootUnit, Some(parent), definition, Some(p), buf)
                prop.setASTUnit(unit)
                parent.addChild(prop)
            })
        })
        dde.linkProperties.foreach(p => {

        })
    }

    private def addObjectProperty(node: ASTNodeImpl, propValue: DialectDomainElement, propName: String, _srcOpt: Option[YPart] = None):Unit = {

        val srcOpt:Option[YPart] = _srcOpt.orElse(propValue.annotations.find(classOf[SourceAST]).map(_.ast))

        val baseUnit = node.astUnit.baseUnit
        val definition = node.definition

        val sourceInfo = SourceInfo().withReferingUnit(node.astUnit)
        srcOpt.foreach(x => sourceInfo.withSources(Seq(x)))

        val propOpt = definition.property(propName)
        val range = propOpt.flatMap(_.range).map(r => {
            if(r.isArray){
                r.array.get.componentType.getOrElse(r)
            }
            else {
                r
            }
        }).getOrElse(BuiltinUniverse.any)
        val ch = new ASTNodeImpl(propValue, baseUnit, Some(node), range, propOpt)
        node.addChild(ch)
        ch.setASTUnit(node.astUnit)
        fillProperties(propValue, ch, propOpt.flatMap(x => x.getExtra(SourcePropertyMapping)))
    }

    private def propertyNameFromId(propId:String):Option[String] = {
        var ind = propId.lastIndexOf("/property/")
        if(ind<0){
            None
        }
        else {
            val pName = propId.substring(ind+"/property/".length)
            Some(pName)
        }
    }

    private def declarationPropertyNameFromId(propId:String):Option[String] = {
        val ind1 = propId.lastIndexOf("#/")
        val ind2 = propId.lastIndexOf("/")
        if(ind1 < 0 || ind2 < 0 || ind2 <= ind1){
            None
        }
        else {
            val pName = propId.substring(ind1 + "#/".length, ind2)
            Some(pName)
        }
    }




}

object DialectProjectBuilder {

    private var instance:Option[DialectProjectBuilder] = None

    def getInstance:DialectProjectBuilder = {

        instance match {
            case Some(x) => x
            case _ => {
                val x = new DialectProjectBuilder()
                instance = Some(x)
                x
            }
        }

    }

}
