package org.mulesoft.language.common.typeInterfaces

/**
  * Document being opened.
  */
case class IOpenedDocument (

  /**
    * Document URI
    */
  var uri: String,

  /**
    * Optional document version.
    */
  var version: Int,

  /**
    * Optional document content
    */
  var text: String
)
