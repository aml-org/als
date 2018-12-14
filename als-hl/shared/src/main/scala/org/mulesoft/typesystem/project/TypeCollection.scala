package org.mulesoft.typesystem.project
import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition
import org.mulesoft.typesystem.nominal_types.Universe

import scala.collection.mutable

class TypeCollection(val id:String, val types:Universe,val annotationTypes:Universe) extends ITypeCollection {

    override val dependencies:mutable.Map[String,DependencyEntry[TypeCollection]] = mutable.Map()

    def registerDependency(dep:DependencyEntry[TypeCollection]):Unit = dependencies(dep.path) = dep
    // $COVERAGE-OFF$
    override def getType(typeName: String): Option[ITypeDefinition] = {
        var tc:TypeCollection = this

        var deps:Iterable[ModuleDependencyEntry[TypeCollection]] = dependencies.values.flatMap({
            case me:ModuleDependencyEntry[TypeCollection] => Some(me)
            case _ => None
        })
        var ind = 0
        var result:Option[ITypeDefinition] = types.`type`(typeName)
        while(result.isEmpty && ind < typeName.length){
            if(typeName.charAt(ind)=='.'){
                var namespace = typeName.substring(0,ind)
                var name = typeName.substring(ind+1)
                deps.find(_.namespace==namespace).foreach(x=>{
                    val t = x.tc.getType(name)
                    result = t
                })
            }
            ind += 1
        }
        result
    }
    // $COVERAGE-ON$
}

object TypeCollection {
    def apply(id:String,types:Universe,annotationTypes:Universe):TypeCollection = new TypeCollection(id, types, annotationTypes)
}
