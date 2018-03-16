package org.mulesoft.typesystem.project

import org.mulesoft.typesystem.nominal_types.AbstractType

import scala.collection.mutable

class TypeCollectionBundle  extends ITypeCollectionBundle {

    val typeCollections:mutable.Map[String,TypeCollection] = mutable.Map()

    def registerTypeCollection(tc:TypeCollection):Unit = typeCollections(tc.id) = tc

    def getType(id: String, name:String): Option[AbstractType] = {
        var path: String = TypeCollectionBundle.pathFromID(id)
        if(path + "#/declarations/"+name == id) {
            typeCollections.get(path).flatMap(_.types.ownType(name))
        }
        else None
    }

    def getAnnotationType(id: String, name:String): Option[AbstractType] = {
        var path: String = TypeCollectionBundle.pathFromID(id)
        if(path + "#/declarations/"+name == id) {
            typeCollections.get(path).flatMap(_.annotationTypes.ownType(name))
        }
        else None
    }


}

object TypeCollectionBundle {
    def pathFromID(id: String):String = {
        var ind = id.indexOf("#")
        var path = if (ind < 0) id else id.substring(ind + 1)
        path
    }
    def apply():TypeCollectionBundle = new TypeCollectionBundle()
}