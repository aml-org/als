package org.mulesoft.language.outline.test

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import upickle.default.write
import DocumentSymbolNode._
import amf.core.model.document.BaseUnit

abstract class StructureTest extends OutlineTest[List[DocumentSymbol]] {

  override def readDataFromAST(unit: BaseUnit, position: Int): List[DocumentSymbol] =
    StructureBuilder.listSymbols(unit)

  override def writeDataToString(data: List[DocumentSymbol]): String =
    write[List[DocumentSymbolNode]](data.map(DocumentSymbolNode.sharedToTransport), 2)

  def emptyData(): List[DocumentSymbol] = Nil
}
