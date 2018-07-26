package org.mulesoft.language.outline

import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.mulesoft.language.test.dtoTypes.StructureNode

import scala.collection.mutable.ListBuffer
import upickle.default.read

abstract class StructureTest extends OutlineTest[Map[String, StructureNode],Map[String, StructureNodeJSON]]{

    override def readDataFromString(dataString: String): Map[String, StructureNode] = read[Map[String, StructureNode]](dataString)

    def emptyData(): Map[String, StructureNode] = Map()

    override def compare(obj1: Map[String, StructureNodeJSON], obj2: Map[String, StructureNodeJSON], prefix1: String, prefix2: String) = {
        var map1 = obj1.map(e=>(e._1,StructureNode.sharedToTransport(e._2)))
        var map2 = obj2.map(e=>(e._1,StructureNode.sharedToTransport(e._2)))
        val diffs = compareStructureNodeMaps(map1, map1, prefix1, prefix2, "/", ListBuffer[Diff](), true)

        diffs
    }
}
