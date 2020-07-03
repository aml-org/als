package org.mulesoft.language.outline.test

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import org.mulesoft.language.outline.test.DocumentSymbolNode._
import upickle.default.write

abstract class BaseStructureTest extends OutlineTest[List[DocumentSymbol]] {

  override def readDataFromAST(unit: BaseUnit, position: Int, definedBy: Option[Dialect]): List[DocumentSymbol] =
    StructureBuilder.listSymbols(unit, definedBy)

  override def writeDataToString(data: List[DocumentSymbol]): String =
    write[List[DocumentSymbolNode]](data.map(DocumentSymbolNode.sharedToTransport), 2)

  def emptyData(): List[DocumentSymbol] = Nil
}
