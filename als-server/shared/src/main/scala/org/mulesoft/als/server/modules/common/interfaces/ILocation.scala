package org.mulesoft.als.server.modules.common.interfaces

import org.mulesoft.als.common.dtoTypes.PositionRange

/**
  * Range in a particular document
  */
trait ILocation {

  /**
    * Document uri
    */
  var uri: String

  /**
    * Optional document version.
    */
  var version: Int

  /**
    * Range in the document.
    */
  var posRange: PositionRange // Range
}
