package org.mulesoft.language.common.dtoTypes

import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON

/**
  * Report for document structure.
  */
case class IStructureReport(
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
    var structure: Map[String, StructureNodeJSON]
) {}
