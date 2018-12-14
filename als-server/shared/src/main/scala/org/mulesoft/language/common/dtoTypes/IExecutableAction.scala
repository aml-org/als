package org.mulesoft.language.common.dtoTypes

/**
  * Actions are being exposed as this outer interface.
  */
trait IExecutableAction {

  /**
    * Unique action ID.
    */
  var id: String

  /**
    * Displayed menu item name
    */
  var name: String

  /**
    * Action target (like editor node, tree viewer etc).
    * The value must be recognizable by action consumers.
    * Some of the standard values are defined in this module.
    */
  var target: String

  /**
    * Whether action has client-side UI part.
    */
  var hasUI: Boolean

  /**
    * Optional action category and potential subcategories.
    * In example, item with a name "itemName" and categories ["cat1", "cat2"]
    * will be displayed as the following menu hierarchy: cat1/cat2/itemName
    */
  var category: Seq[String]

  /**
    * Optional label, will be used instead of name for display purpose
    */
  var label: String
}
