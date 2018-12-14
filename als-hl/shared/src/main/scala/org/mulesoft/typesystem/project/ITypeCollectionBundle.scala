package org.mulesoft.typesystem.project

import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition

import scala.collection.Map

trait ITypeCollectionBundle {

    val typeCollections:Map[String,ITypeCollection]

    def getType(id:String,name:String):Option[ITypeDefinition]

    def getAnnotationType(id:String,name:String):Option[ITypeDefinition]

}
