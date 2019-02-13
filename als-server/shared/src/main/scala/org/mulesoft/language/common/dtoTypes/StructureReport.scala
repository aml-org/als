package org.mulesoft.language.common.dtoTypes

import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON

/**
  * Report for document structure.
  */
case class StructureReport(

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
                             var structure: List[DocumentSymbol]
                           ) {}
