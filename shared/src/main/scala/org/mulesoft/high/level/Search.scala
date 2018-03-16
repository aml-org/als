package org.mulesoft.high.level

import org.mulesoft.high.level.interfaces.{IASTUnit, IHighLevelNode}
import org.mulesoft.typesystem.project.ModuleDependencyEntry

import scala.collection.mutable.ListBuffer



object Search {

    def getDeclarations(unit:IASTUnit,typeName:String):Seq[Declaration] = {

        var result:ListBuffer[Declaration] = ListBuffer()
        var ownDeclarations: Seq[IHighLevelNode] = extractDeclarations(unit, typeName)
        ownDeclarations.foreach(decl=>result += new Declaration(decl,unit,None))
        var libsToSearch:Iterable[ModuleDependencyEntry[IASTUnit]]
                = unit.dependencies.values.flatMap(x =>
            if (x.isModule) Some(x.asInstanceOf[ModuleDependencyEntry[IASTUnit]]) else None)

        libsToSearch.foreach(libDependency=>{
            val libUnit = libDependency.tc
            var libDeclarations = extractDeclarations(libUnit,typeName)
            libDeclarations.foreach(decl=>result += new Declaration(decl,libUnit,Option(libDependency.namespace)))
        })
        result
    }

    private def extractDeclarations(unit: IASTUnit, typeName: String) = {
        var ownDeclarations: Seq[IHighLevelNode] = unit.rootNode.children.flatMap(x => {
            if (!x.isElement) {
                None
            }
            else if (x.asElement.get.definition.isAssignableFrom(typeName)) {
                x.asElement
            }
            else {
                None
            }
        })
        ownDeclarations
    }
}

class Declaration(val node:IHighLevelNode, val declaringUnit:IASTUnit, val namspace:Option[String]) {}