package org.mulesoft.typesystem.project

import org.mulesoft.typesystem.nominal_types.AbstractType

import scala.collection.mutable

class TypeCollectionBundle  extends ITypeCollectionBundle {

    val typeCollections:mutable.Map[String,TypeCollection] = mutable.Map()

    def registerTypeCollection(tc:TypeCollection):Unit = typeCollections(tc.id) = tc

    def getType(id: String, name:String): Option[AbstractType] = {
        var path: String = TypeCollectionBundle.pathFromID(id)
        Option(path).flatMap(typeCollections.get).flatMap(_.types.ownType(name))
    }

    def getAnnotationType(id: String, name:String): Option[AbstractType] = {
        var path: String = TypeCollectionBundle.pathFromID(id)
        typeCollections.get(path).flatMap(_.annotationTypes.ownType(name))
    }


}

object TypeCollectionBundle {
    def pathFromID(id: String):String = {
        if(id==null){
            null
        }
        else {
            var ind = id.indexOf("#")
            var path = if (ind < 0) id else id.substring(0, ind)
            path
        }
    }
    def apply():TypeCollectionBundle = new TypeCollectionBundle()
}