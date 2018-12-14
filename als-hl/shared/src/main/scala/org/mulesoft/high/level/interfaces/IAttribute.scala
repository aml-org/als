package org.mulesoft.high.level.interfaces

import org.mulesoft.typesystem.nominal_interfaces.ITypeDefinition

import scala.concurrent.Future

trait IAttribute extends IParseResult {

    def name: String

    def definition: Option[ITypeDefinition]

    def value: Option[Any]

    //def plainValue: Any

    //def setKey(k: String): Unit

    def setValue(newValue: Any): Future[IModelModificationResult]

    def modify(newValue: Any):Unit

//    def setValues(values: Seq[Any]): Unit
//
//    def addValue(value: Any): Unit

    //def remove(): Unit

    //def isEmpty: Boolean

    //  def owningWrapper(): {   var node: BasicNode
    //  var property: String
    //}
    //def findReferencedValue: Option[IHighLevelNode]

    //def isAnnotatedScalar: Boolean

    def annotations: Seq[IAttribute]
}
