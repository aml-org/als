package org.mulesoft.als.server.modules.common

import org.mulesoft.als.common.dtoTypes.PositionRange

/**
  * Single text edit in a document.
  */
case class TextEdit(
    /**
      * Range to replace. Range start==end==0 => insert into the beginning of the document,
      * start==end==document end => insert into the end of the document
      */
    var range: PositionRange,
    /**
      * Text to replace given range with.
      */
    var text: String
)
