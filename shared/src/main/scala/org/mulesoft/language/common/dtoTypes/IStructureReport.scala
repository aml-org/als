package org.mulesoft.language.common.dtoTypes

/**
  * Report for document structure.
  */
case class IStructureReport (

 /**
   * Document uri.
   */
 var uri: String,

 /**
   * Optional document version.
   */
 var version: Int,

 /**
   * Document structure.
   */
 var structure: Map[String, StructureNode]
)
{

}
