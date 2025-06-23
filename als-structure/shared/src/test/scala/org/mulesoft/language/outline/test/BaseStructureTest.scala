package org.mulesoft.language.outline.test

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureBuilder}
import org.mulesoft.language.outline.test.DocumentSymbolNode._
import upickle.default.write

abstract class BaseStructureTest extends OutlineTest[List[DocumentSymbol]] {

  override def readDataFromAST(unit: BaseUnit, documentDefinition: DocumentDefinition): List[DocumentSymbol] =
    StructureBuilder.listSymbols(unit, documentDefinition)

  override def writeDataToString(data: List[DocumentSymbol]): String =
    write[List[DocumentSymbolNode]](data.map(DocumentSymbolNode.sharedToTransport), 2)

  def emptyData(): List[DocumentSymbol] = Nil
}
