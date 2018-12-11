package org.mulesoft.typesystem.project

import org.mulesoft.typesystem.nominal_interfaces.{ITypeDefinition, IUniverse}

import scala.collection.Map

trait ITypeCollection {

    val id:String

    val types:IUniverse

    val annotationTypes:IUniverse

    val dependencies:Map[String,DependencyEntry[_ <: ITypeCollection]]

    def getType(typeName:String):Option[ITypeDefinition]
}
