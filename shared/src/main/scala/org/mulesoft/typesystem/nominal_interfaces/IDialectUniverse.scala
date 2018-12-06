package org.mulesoft.typesystem.nominal_interfaces

import org.mulesoft.typesystem.project.ITypeCollectionBundle

import scala.collection.Map

trait IDialectUniverse extends IUniverse {

    def root: Option[ITypeDefinition]

    def library: Option[ITypeDefinition]

    def fragments:Map[String,ITypeDefinition]

    def isReferable(typeName:String):Boolean
}
