package org.mulesoft.language.common.dtoTypes

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
case class DetailsItem(title: String,
                       description: String,
                       `type`: String,
                       error: String,
                       children: Seq[DetailsItem],
                       id: String)
    extends IDetailsItem {}

/**
  * Details item having a value text.
  */
case class DetailsValuedItem(title: String,
                             description: String,
                             `type`: String,
                             error: String,
                             children: Seq[DetailsItem],
                             id: String,
                             valueText: String)
    extends IDetailsItem {}

/**
  * Details item having potential value options
  */
case class DetailsItemWithOptions(title: String,
                                  description: String,
                                  `type`: String,
                                  error: String,
                                  children: Seq[DetailsItem],
                                  id: String,
                                  options: Seq[String])
    extends IDetailsItem {}
