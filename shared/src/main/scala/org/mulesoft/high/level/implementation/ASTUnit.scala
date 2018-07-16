package org.mulesoft.high.level.implementation

import amf.core.model.document.BaseUnit
import org.mulesoft.high.level.interfaces.IASTUnit
import org.mulesoft.positioning.{IPositionsMapper, PositionsMapper}
import org.mulesoft.typesystem.nominal_types.Universe
import org.mulesoft.typesystem.project.{DependencyEntry, TypeCollection}

import scala.collection.{Map, mutable}

class ASTUnit(_baseUnit:BaseUnit, typeCollection:TypeCollection, _project:Project) extends IASTUnit {

    private var _dependencies: mutable.Map[String,DependencyEntry[ASTUnit]] = mutable.Map()

    private var _dependants: mutable.Map[String,DependencyEntry[ASTUnit]] = mutable.Map()

    private var _rootNode:Option[ASTNodeImpl] = None

    private var _positionsMapper:IPositionsMapper = PositionsMapper(_baseUnit.location().getOrElse("")).withText(_baseUnit.raw.getOrElse(""))

    def path:String = _baseUnit.id

    override def dependencies: Map[String, DependencyEntry[ASTUnit]] = _dependencies

    override def dependants: Map[String, DependencyEntry[ASTUnit]] = _dependants

    override def universe: Universe = typeCollection.types

    override def baseUnit: BaseUnit = _baseUnit

    override def types: TypeCollection = typeCollection

    override def project: Project = _project

    override def rootNode: ASTNodeImpl = _rootNode.orNull

    def setRootNode(n:ASTNodeImpl):Unit = _rootNode = Option(n)

    def registerDependency(dep:DependencyEntry[ASTUnit]):Unit = {
        _dependencies.put(dep.path,dep)
    }

    def registerReverseDependency(dep:DependencyEntry[ASTUnit]):Unit = {
        _dependants.put(dep.path,dep)
    }

    def positionsMapper: IPositionsMapper = _positionsMapper

    def setPositionsMapper(pm:IPositionsMapper):Unit = _positionsMapper = pm

    def initSources():Unit = _rootNode.foreach(_.initSources(Some(this),None))

    def text: String = _baseUnit.raw.orNull

    def resolve(p: String): Option[ASTUnit] = _project.resolve(path,p)

    def resolvePath(p: String): Option[String] = _project.resolvePath(path,p)
}

object ASTUnit {
    def apply(baseUnit:BaseUnit, typeCollection:TypeCollection, _project:Project):ASTUnit = new ASTUnit(baseUnit,typeCollection,_project)
}
