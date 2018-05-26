package org.mulesoft.language.common.dtoTypes

/**
  * Find Declarations request
  */
case class IFindRequest (

  /**
    * Document URI
    */
  var uri: String,

  /**
    * Document position.
    */
  var position: Int
)
