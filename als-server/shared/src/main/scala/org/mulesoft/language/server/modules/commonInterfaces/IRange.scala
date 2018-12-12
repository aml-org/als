package org.mulesoft.language.server.server.modules.commonInterfaces

/**
  * Range of positions in text.
  */
trait IRange {

  /**
    * Range start
    */
  var start: IPoint

  /**
    * Range end
    */
  var end: IPoint
}
