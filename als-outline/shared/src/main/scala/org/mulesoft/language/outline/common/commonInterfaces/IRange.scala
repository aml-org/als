package org.mulesoft.language.outline.common.commonInterfaces

/**
  * Range in the document.
  */
case class IRange(
    /**
      * Range start position, counting from 0
      */
    var start: Int,
    /**
      * Range end position, counting from 0
      */
    var end: Int
) {}
