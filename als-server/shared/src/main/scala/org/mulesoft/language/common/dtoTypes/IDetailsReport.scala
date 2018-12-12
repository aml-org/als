package org.mulesoft.language.common.dtoTypes

/**
  * Report for outline item details.
  */
trait IDetailsReport {

  /**
    * Document uri.
    */
  var uri: String

  /**
    * Cursor position in the document, starting from 0.
    */
  var position: Int

  /**
    * Optional document version.
    */
  var version: Int

  /**
    * Details root item.
    */
  var details: IDetailsItem
}
