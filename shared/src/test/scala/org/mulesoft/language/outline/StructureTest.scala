package org.mulesoft.language.outline

import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.mulesoft.language.test.dtoTypes.StructureNode

import scala.collection.mutable.ListBuffer
import upickle.default.read

abstract class StructureTest extends OutlineTest[Map[String, StructureNodeJSON],Map[String, StructureNode]]{

    override def readDataFromString(dataString: String): Map[String, StructureNode] = read[Map[String, StructureNode]](dataString)

    override def compare(map1: Map[String, StructureNodeJSON], obj2: Map[String, StructureNode], prefix1: String, prefix2: String) = {
        var obj1 = map1.map(e=>(e._1,StructureNode.sharedToTransport(e._2)))
        val diffs = compareStructureNodeMaps(obj1, obj2, prefix1, prefix2, "/", ListBuffer[Diff](), true)

        diffs
    }
}
