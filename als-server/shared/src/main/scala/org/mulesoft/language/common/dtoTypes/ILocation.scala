package org.mulesoft.language.common.dtoTypes

import common.dtoTypes.PositionRange

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
