package org.mulesoft.als.server.modules.common.interfaces

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
