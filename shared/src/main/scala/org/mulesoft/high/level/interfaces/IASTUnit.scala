package org.mulesoft.high.level.interfaces

import amf.core.model.document.BaseUnit
import org.mulesoft.positioning.IPositionsMapper
import org.mulesoft.typesystem.nominal_interfaces.IUniverse
import org.mulesoft.typesystem.project.{DependencyEntry, ITypeCollection}

import scala.collection.Map

trait IASTUnit {

    def universe:IUniverse

    def baseUnit:BaseUnit

    def dependencies: Map[String,DependencyEntry[_ <: IASTUnit]]

    def dependants: Map[String,DependencyEntry[_ <: IASTUnit]]

    def types: ITypeCollection

    def project:IProject

    def rootNode:IHighLevelNode

    def path:String

    def positionsMapper:IPositionsMapper

    def text:String

    def resolve(path:String): Option[IASTUnit]
}
