package org.mulesoft.language.outline.test

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import upickle.default.read

import scala.collection.mutable.ListBuffer

abstract class StructureTest extends OutlineTest[Map[String, StructureNode]]{

    override def readDataFromAST(project:IProject,position:Int): Map[String, StructureNode] ={
        val sharedStructureMap: Map[String, StructureNodeJSON] = this.getStructureFromAST(project.rootASTUnit.rootNode, format, position)

        val transportStructureMap:Map[String, StructureNode] = sharedStructureMap.map(e => (e._1,StructureNode.sharedToTransport(e._2)))

        transportStructureMap
    }

    override def readDataFromString(dataString:String):Map[String, StructureNode] = read[Map[String,StructureNode]](dataString)

    def emptyData(): Map[String, StructureNode] = Map()

    override def compare(obj1:Map[String, StructureNode], obj2:Map[String, StructureNode],prefix1:String,prefix2:String) = {
        val diffs = compareStructureNodeMaps(obj1,obj2,prefix1,prefix2,"/",ListBuffer[Diff](),true)

        diffs
    }
}
