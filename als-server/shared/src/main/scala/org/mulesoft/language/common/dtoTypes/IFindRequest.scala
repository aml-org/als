package org.mulesoft.language.common.dtoTypes

import common.dtoTypes.Position

/**
  * Find Declarations request
  */
case class IFindRequest(
    /**
      * Document URI
      */
    var uri: String,
    /**
      * Document position.
      */
    var position: Position
)
