package org.mulesoft.language.outline.common.commonInterfaces

/**
  * Single text edit in a document.
  */
case class ITextEdit(
    /**
      * Range to replace. Range start==end==0 => insert into the beginning of the document,
      * start==end==document end => insert into the end of the document
      */
    var range: IRange,
    /**
      * Text to replace given range with.
      */
    var text: String
) {}
