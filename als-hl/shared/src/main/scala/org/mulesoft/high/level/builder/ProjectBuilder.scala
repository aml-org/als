package org.mulesoft.high.level.builder

import amf.core.annotations.{Aliases, SourceVendor}
import amf.core.metamodel.document.DocumentModel
import amf.core.model.document.{BaseUnit, ExternalFragment, Fragment, Module}
import amf.core.remote.Vendor
import amf.plugins.document.vocabularies.model.document.{
  DialectInstance,
  DialectInstanceFragment,
  DialectInstanceLibrary
}
import org.mulesoft.high.level.dialect.DialectProjectBuilder
import org.mulesoft.high.level.implementation.{ASTUnit, AlsPlatform, Project}
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.typesystem.TypeBuilder
import org.mulesoft.typesystem.project._

import scala.collection.mutable.ListBuffer
import scala.collection.{Map, mutable}

object ProjectBuilder {

  def buildProject(rootUnit: BaseUnit, alsPlatform: AlsPlatform): IProject = {
    rootUnit match {
      case di: DialectInstance          => DialectProjectBuilder.getInstance.buildProject(di, alsPlatform)
      case dil: DialectInstanceLibrary  => DialectProjectBuilder.getInstance.buildProject(dil, alsPlatform)
      case dif: DialectInstanceFragment => DialectProjectBuilder.getInstance.buildProject(dif, alsPlatform)
      case _                            => buildProjectInternal(rootUnit, alsPlatform)
    }
  }

  def buildProjectInternal(rootUnit: BaseUnit, alsPlatform: AlsPlatform): IProject = {

    val formatOpt = determineFormat(rootUnit)
    if (formatOpt.isEmpty) {
      throw new Error("Unable to determine input format")
    }
    val format = formatOpt.get
    ASTFactoryRegistry.getFactory(format) match {
      case Some(factory) =>
        val units    = listUnits(rootUnit)
        val bundle   = TypeBuilder.buildTypes(units, factory)
        val project  = Project(bundle, format, alsPlatform)
        val astUnits = createASTUnits(units, bundle, project)
        astUnits.values.foreach(project.addUnit)
        initASTUnits(astUnits, bundle, factory)
        val rootUnitPath = TypeBuilder.normalizedPath(rootUnit)
        project.setRootUnit(astUnits(rootUnit.location().getOrElse(rootUnitPath)))
        project
      case _ => throw new Error("Unknown format: " + format)
    }
  }
  def createASTUnits(units: Map[String, BaseUnit],
                     bundle: TypeCollectionBundle,
                     project: Project): Map[String, ASTUnit] = {

    val result: mutable.Map[String, ASTUnit] = mutable.Map()
    units.values.foreach(bu => {
      val unitPath = TypeBuilder.normalizedPath(bu)
      val tc       = bundle.typeCollections(unitPath)
      val astUnit  = ASTUnit(bu, tc, project)
      result.put(astUnit.path, astUnit)
    })
    result
  }

  def initASTUnits(astUnits: Map[String, ASTUnit], bundle: TypeCollectionBundle, factory: IASTFactory): Unit = {
    // TODO: Check if it's correct to loop for both astUnit.baseUnit and also aliases
    for { astUnit <- astUnits.values } {
      TypeBuilder
        .getReferences(astUnit.baseUnit)
        .foreach(u => {
          val ref             = u.reference
          val referredAstUnit = astUnits(ref)
          referredAstUnit.baseUnit match {
            case _: Module =>
              astUnit.baseUnit.annotations
                .find(classOf[Aliases])
                .map(_.aliases)
                .foreach(aliases =>
                  aliases
                    .filter(usesEntry => usesEntry._2._1 == ref)
                    .foreach(usesEntry => {
                      val namespace           = usesEntry._1
                      val referringModulePath = usesEntry._2._1
                      val libPath             = usesEntry._2._2
                      astUnits
                        .get(referringModulePath)
                        .foreach(referringAstUnit => {
                          val dep = new ModuleDependencyEntry(libPath, referredAstUnit, namespace, libPath)
                          astUnit.registerDependency(dep)
                          val reverseDep =
                            new ModuleDependencyEntry(referringAstUnit.path, referringAstUnit, namespace, libPath)
                          referredAstUnit.registerReverseDependency(reverseDep)
                        })
                    }))

            // TODO aliases validity filter needed
            case _: ExternalFragment =>
              val dep = new DependencyEntry(ref, referredAstUnit)
              astUnit.registerDependency(dep)
              val reverseDep = new DependencyEntry(astUnit.path, astUnit)
              referredAstUnit.registerReverseDependency(reverseDep)
            case _: Fragment =>
              val dep = new FragmentDependencyEntry(ref, referredAstUnit)
              astUnit.registerDependency(dep)
              val reverseDep = new FragmentDependencyEntry(astUnit.path, astUnit)
              referredAstUnit.registerReverseDependency(reverseDep)
            case _ =>
          }
        })
    }

    for { astUnit <- astUnits.values } {
      val hlNode = NodeBuilder.buildAST(astUnit.baseUnit, bundle, factory)
      hlNode.foreach(x => {
        astUnit.setRootNode(x)
        x.setASTUnit(astUnit)
      })
      astUnit.initSources()
    }
  }

  def determineFormat(baseUnit: BaseUnit): Option[Vendor] = {
    var formatOpt = baseUnit.annotations.find(classOf[SourceVendor]).map(_.vendor)
    if (formatOpt.isEmpty) {
      Option(baseUnit.fields.getValue(DocumentModel.Encodes)) match {
        case Some(value) => formatOpt = value.value.annotations.find(classOf[SourceVendor]).map(_.vendor)
        case _           =>
      }
    }
    formatOpt
  }

  private def listUnits(rootUnit: BaseUnit,
                        processed: mutable.Map[String, BaseUnit] = mutable.Map()): Map[String, BaseUnit] = {
    val id = TypeBuilder.normalizedPath(rootUnit)
    if (!processed.contains(id)) {
      processed(id) = rootUnit
      rootUnit.references.foreach { unit =>
        listUnits(unit, processed)
      }
    }
    processed
  }

}
