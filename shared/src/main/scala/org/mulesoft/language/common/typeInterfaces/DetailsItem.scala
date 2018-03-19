package org.mulesoft.language.common.typeInterfaces

/**
  * The node of details tree converted to JSON
  */
sealed trait IDetailsItem {

 /**
   * Node title.
   */
 def title: String

 /**
   * Node description
   */
 def description: String

 /**
   * Node type name
   */
 def `type`: String

 /**
   * Error, associated with the node.
   */
 def error: String

 /**
   * Node children.
   */
 def children: Seq[DetailsItem]

 /**
   * Node ID.
   */
 def id: String
}

/**
  * The node of details tree converted to JSON
  */
case class DetailsItem (

  /**
    * Node title.
    */
  title: String,

  /**
    * Node description
    */
  description: String,

  /**
    * Node type name
    */
  `type`: String,

  /**
    * Error, associated with the node.
    */
  error: String,

  /**
    * Node children.
    */
  children: Seq[DetailsItem],

  /**
    * Node ID.
    */
  id: String

) extends IDetailsItem
{

}

  /**
    * Details item having a value text.
    */
case class DetailsValuedItem (

 /**
   * Node title.
   */
 title: String,

 /**
   * Node description
   */
 description: String,

 /**
   * Node type name
   */
 `type`: String,

 /**
   * Error, associated with the node.
   */
 error: String,

 /**
   * Node children.
   */
 children: Seq[DetailsItem],

 /**
   * Node ID.
   */
 id: String,

  /**
    * Value text.
    */
  valueText: String

) extends IDetailsItem
{

}

/**
  * Details item having potential value options
  */
case class DetailsItemWithOptions (

  /**
    * Node title.
    */
  title: String,

  /**
    * Node description
    */
  description: String,

  /**
    * Node type name
    */
  `type`: String,

  /**
    * Error, associated with the node.
    */
  error: String,

  /**
    * Node children.
    */
  children: Seq[DetailsItem],

  /**
    * Node ID.
    */
  id: String,

  /**
    * Potential options.
    */
  options: Seq[String]

) extends IDetailsItem
{

}