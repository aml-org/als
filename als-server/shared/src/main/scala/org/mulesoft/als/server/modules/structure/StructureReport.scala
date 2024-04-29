package org.mulesoft.als.server.modules.structure

import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol

/** Report for document structure.
  */
case class StructureReport(
    /** Document uri.
      */
    var uri: String,

    /** Optional document version.
      */
    var version: Int,

    /** Document structure.
      */
    var structure: List[DocumentSymbol]
) {}
