package org.mulesoft.high.level.dialect

import amf.core.model.domain.DomainElement
import amf.plugins.document.vocabularies.metamodel.domain.NodeMappingModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{DocumentMapping, NodeMapping, PropertyMapping}
import org.mulesoft.typesystem.dialects.{BuiltinUniverse, DialectUniverse}
import org.mulesoft.typesystem.dialects.extras.{Declaration, RootType, SourceNodeMapping, SourcePropertyMapping}
import org.mulesoft.typesystem.nominal_interfaces.{IDialectUniverse, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.nominal_types.{StructuredType, Union, Universe}
import org.mulesoft.typesystem.typesystem_interfaces.Extra

import scala.collection.mutable

object DialectUniverseBuilder {

    def buildUniverse(dialect:Dialect):DialectUniverse = {

        val result = new DialectUniverse(dialect.name().value(), Option(BuiltinUniverse.getInstance), dialect.version().value())

        initNodeMappingTypes(dialect, result)
        fillProperties(result)

        val docs = dialect.documents()
        Option(docs.root()).foreach(doc=>{

            val rootType = processDocument(result, doc)
            result.withRoot(rootType)
        })
        Option(docs.library()).foreach(doc=>{
            val libType = processDocument(result, doc)
            result.withLibrary(libType)
        })
        Option(docs.fragments()).foreach(_.foreach(doc=>{
            val t = processDocument(result, doc)
            result.withFragment(t)
        }))
        result
    }

    def getOrCreateType(de: DomainElement, universe:Universe):Option[ITypeDefinition] = {
        if (de.meta == NodeMappingModel) {
            val nodeMapping = de.asInstanceOf[NodeMapping]
            val name = nodeMapping.name.value()
            val existingOpt = universe.`type`(name)
            if(existingOpt.isDefined){
                existingOpt
            }
            else {
                val id = nodeMapping.id
                val t = new StructuredType(name, universe, id)
                universe.register(t)
                t.putExtra(SourceNodeMapping, nodeMapping)
                t.addSuperType(BuiltinUniverse.any)
                Some(t)
            }
        }
        else {
            None
        }
    }

    private def processDocument(universe: Universe, doc: DocumentMapping):StructuredType = {
        val name = doc.documentName().value()
        val rootType = new StructuredType(name, universe, doc.id)
        rootType.putExtra(RootType)
        Option(doc.declaredNodes()).foreach(_.foreach(s => {
            val pName = s.name().value()
            Option(s.mappedNode()).flatMap(_.option()).flatMap(userDataTypeToString).flatMap(universe.`type`).foreach(t => {
                val prop = rootType.addProperty(pName, t).withMultiValue(true)
                prop.putExtra(Declaration)
            })
        }))
        val encodedType = Option(doc.encoded()).flatMap(_.option()).flatMap(userDataTypeToString).flatMap(universe.`type`)
        encodedType.foreach(rootType.addSuperType)
        rootType
    }

    private def initNodeMappingTypes(dialect: Dialect, universe: Universe):Unit = {
        dialect.declares.foreach(de => {
            if (de.meta == NodeMappingModel) {
                val nodeMapping = de.asInstanceOf[NodeMapping]
                val name = nodeMapping.name.value()
                val id = nodeMapping.id
                val t = new StructuredType(name, universe, id)
                universe.register(t)
                t.putExtra(SourceNodeMapping,nodeMapping)
                t.addSuperType(BuiltinUniverse.any)
            }
        })
    }

    private def fillProperties(universe: Universe):Unit = {

        universe.types.foreach(t=>{
            t.getExtra(SourceNodeMapping).foreach(nm => {
                Option(nm.propertiesMapping()).foreach(_.foreach(pm=>{
                    processProperty(t.asInstanceOf[StructuredType], pm)
                }))
            })
        })
    }

    private def processProperty(t: StructuredType, pm: PropertyMapping):Unit = {
        val universe = t.universe
        val pName = pm.name().value()
        val minCount = pm.minCount().value()
        var range:Option[ITypeDefinition] = Some(BuiltinUniverse.any)

        range = Option(pm.literalRange()).flatMap(_.option()).map(primitiveDataTypeToString).flatMap(BuiltinUniverse.getInstance.`type`)

        Option(pm.objectRange()).foreach(objectRanges => {
            val rangeTypes = objectRanges.flatMap(_.option()).flatMap(userDataTypeToString).flatMap(universe.`type`)
            if(rangeTypes.lengthCompare(1) == 0){
                range = rangeTypes.headOption
            }
            else if(rangeTypes.lengthCompare(1) > 0){
                val union = new Union(pm.id, t.universe, pm.id)
                union.setOptions(rangeTypes)
                range = Some(union)
            }
        })
        range.foreach(r=>{
            val prop = t.addProperty(pName, r)
            if(minCount > 0){
                prop.withRequired(true)
            }
            Option(pm.enum()).filter(_.nonEmpty).map(x => {
                x.map(_.value().toString())
            }).foreach( x => {
                prop.withEnumOptions(x)
            })
            prop.putExtra(SourcePropertyMapping,pm)
        })
    }

    private def primitiveDataTypeToString(dataType: String): String = {
        dataType.substring(dataType.indexOf("#") + 1)
    }

    private def userDataTypeToString(dataType: String): Option[String] = {
        val ind = dataType.indexOf("#/declarations/")
        if(ind<0){
            None
        }
        else {
            Some(dataType.substring(ind + "#/declarations/".length))
        }
    }
}

class UnderConstruction extends Extra[UnderConstruction]{
    override def name: String = "UnderConstruction"

    override def clazz: Class[UnderConstruction] = classOf[UnderConstruction]

    override def default:Option[UnderConstruction] = None
}

object UnderConstruction extends UnderConstruction {}
