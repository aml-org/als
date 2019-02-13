package org.mulesoft.language.outline.test

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import upickle.default.write
import DocumentSymbolNode._

abstract class StructureTest extends OutlineTest[List[DocumentSymbol]] {

  override def readDataFromAST(project: IProject, position: Int): List[DocumentSymbol] =
    this.getDocumentSymbolFromAST(project.rootASTUnit.rootNode, format, position)

  override def writeDataToString(data: List[DocumentSymbol]): String =
    write[List[DocumentSymbolNode]](data.map(DocumentSymbolNode.sharedToTransport), 2)

  def emptyData(): List[DocumentSymbol] = Nil
}
