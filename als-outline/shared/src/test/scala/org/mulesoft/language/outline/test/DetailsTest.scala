//package org.mulesoft.language.outline.test
//
//import org.mulesoft.high.level.interfaces.IProject
//import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
//import upickle.default.read
//
//import scala.collection.mutable.ListBuffer
//
//abstract class DetailsTest extends OutlineTest[StructureNode]{
//
//    override def readDataFromAST(project:IProject,position:Int): StructureNode ={
//        val node:StructureNodeJSON = this.getStructureFromAST(project.rootASTUnit.rootNode, format, position)
//
//        val transportNode:StructureNode =  StructureNode.sharedToTransport(node)
//        transportNode
//    }
//
//    override def readDataFromString(dataString:String):StructureNode = read[StructureNode](dataString)
//
//    def emptyData(): StructureNode = null
//
//    override def compare(obj1:StructureNode, obj2:StructureNode,prefix1:String,prefix2:String) = {
//        val diffs = compareStructureNodes(obj1,obj2,prefix1,prefix2,"/",ListBuffer[Diff]())
//
//        diffs
//    }
//}
