package org.mulesoft.high.level.builder

import amf.core.annotations.{Aliases, SourceVendor}
import amf.core.metamodel.document.DocumentModel
import amf.core.model.document.{BaseUnit, ExternalFragment, Fragment, Module}
import amf.core.remote.Vendor
import org.mulesoft.high.level.implementation.{ASTUnit, Project}
import org.mulesoft.high.level.interfaces.{IFSProvider, IProject}
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.project._

import scala.collection.mutable.ListBuffer
import scala.collection.{Map, mutable}

object ProjectBuilder {

    def buildProject(rootUnit:BaseUnit,fsResolver:IFSProvider):IProject = {

        var formatOpt = determineFormat(rootUnit)
        if (formatOpt.isEmpty) {
            throw new Error("Unable to determine input format")
        }
        var format = formatOpt.get
        ASTFactoryRegistry.getFactory(format) match {
            case Some(factory) =>
                var units = listUnits(rootUnit)
                var bundle = TypeBuilder.buildTypes(units,factory)
                var project = Project(bundle,format,fsResolver)
                var astUnits = createASTUnits(units,bundle,project)
                initASTUnits(astUnits,bundle,factory)
                project.setRootUnit(astUnits(rootUnit.location().getOrElse(rootUnit.id)))
                astUnits.values.foreach(project.addUnit)
                project
            case _ => throw new Error("Unknown format: " + format)
        }
    }
    def createASTUnits(
                units:Map[String,BaseUnit],
                bundle:TypeCollectionBundle,
                project:Project):Map[String,ASTUnit] = {

        var result: mutable.Map[String, ASTUnit] = mutable.Map()
        units.values.foreach(bu => {
            val tc = bundle.typeCollections(bu.id)
            var astUnit = ASTUnit(bu, tc, project)
            result.put(astUnit.path, astUnit)
        })
        result
    }

    def initASTUnits(
                astUnits:Map[String,ASTUnit],
                bundle:TypeCollectionBundle,
                factory:IASTFactory):Unit = {

        for (astUnit <- astUnits.values) {
            astUnit.baseUnit.references.foreach(ref=>{
                var referedAstUnit = astUnits(ref.id)
                referedAstUnit.baseUnit match {
                    case m:Module =>
                        var aliases = m.annotations.find(classOf[Aliases])
                            .map(_.aliases).getOrElse((null,(null,null))::Nil)
                        //TODO aliases validity filter needed
                        for(usesEntry <- aliases){
                            val namespace = usesEntry._1
                            val referingModulePath = usesEntry._2._1
                            val libPath = usesEntry._2._2
                            astUnits.get(referingModulePath).foreach(referingAstUnit=>{
                                var dep = new ModuleDependencyEntry(ref.id,referedAstUnit,namespace,libPath)
                                referingAstUnit.registerDependency(dep)
                                var reverseDep = new ModuleDependencyEntry(referingAstUnit.path,referingAstUnit,namespace,libPath)
                                referedAstUnit.registerReverseDependency(reverseDep)
                            })
                        }
                    case ef:ExternalFragment =>
                        var dep = new DependencyEntry(ref.id,referedAstUnit)
                        astUnit.registerDependency(dep)
                        var reverseDep = new DependencyEntry(astUnit.path,astUnit)
                        referedAstUnit.registerReverseDependency(reverseDep)
                    case f: Fragment =>
                        var dep = new FragmentDependencyEntry(ref.id,referedAstUnit)
                        astUnit.registerDependency(dep)
                        var reverseDep = new FragmentDependencyEntry(astUnit.path,astUnit)
                        referedAstUnit.registerReverseDependency(reverseDep)
                    case _ =>
                }
            })
        }

        for (astUnit <- astUnits.values) {
            var hlNode = NodeBuilder.buildAST(astUnit.baseUnit,bundle,factory)
            hlNode.foreach(x=>{
                astUnit.setRootNode(x)
                x.setASTUnit(astUnit)
            })
            astUnit.initSources()
        }
    }

    def determineFormat(baseUnit:BaseUnit):Option[Vendor] = {
        var formatOpt = baseUnit.annotations.find(classOf[SourceVendor]).map(_.vendor)
        if(formatOpt.isEmpty){
            Option(baseUnit.fields.getValue(DocumentModel.Encodes)) match {
                case Some(value) => formatOpt = value.value.annotations.find(classOf[SourceVendor]).map(_.vendor)
                case _ =>
            }
        }
        formatOpt
    }

    private def listUnits(rootUnit:BaseUnit):Map[String,BaseUnit] = {
        var processed: mutable.Map[String, BaseUnit] = mutable.Map()
        var toProcess: ListBuffer[BaseUnit] = ListBuffer() += rootUnit
        var i:Int = 0
        while(toProcess.lengthCompare(i)>0) {
            var unit = toProcess(i)
            var id = unit.id

            processed(id) = unit
            var newRefs = unit.references.filter(ref => !processed.contains(ref.id))
            newRefs.foreach(ref => {
                toProcess += ref
                processed(ref.id) = ref
            })
            i += 1
        }
        processed
    }

}
