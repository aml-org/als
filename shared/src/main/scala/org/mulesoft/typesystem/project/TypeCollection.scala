package org.mulesoft.typesystem.project
import org.mulesoft.typesystem.nominal_types.Universe

import scala.collection.mutable

class TypeCollection(val id:String, val types:Universe,val annotationTypes:Universe) extends ITypeCollection {

    override val dependencies:mutable.Map[String,DependencyEntry[TypeCollection]] = mutable.Map()

    def registerDependency(dep:DependencyEntry[TypeCollection]):Unit = dependencies(dep.path) = dep
}

object TypeCollection {
    def apply(id:String,types:Universe,annotationTypes:Universe):TypeCollection = new TypeCollection(id, types, annotationTypes)
}
