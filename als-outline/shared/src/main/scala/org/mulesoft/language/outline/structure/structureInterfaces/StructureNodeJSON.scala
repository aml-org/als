package org.mulesoft.language.outline.structure.structureInterfaces

trait StructureNodeJSON {

  /**
    * Node label text to be displayed.
    */
  def text: String

  /**
    * Node type label, if any.
    */
  def typeText: Option[String]

  /**
    * Node icon. Structure module is not setting up, how icons are represented in the client
    * system, or what icons exist,
    * instead the client is responsible to configure the mapping from nodes to icon identifiers.
    */
  def icon: String

  /**
    * Text style of the node. Structure module is not setting up, how text styles are represented in the client
    * system, or what text styles exist,
    * instead the client is responsible to configure the mapping from nodes to text styles identifiers.
    */
  def textStyle: String

  /**
    * Unique node identifier.
    */
  def key: String

  /**
    * Node start position from the beginning of the document.
    */
  def start: Int

  /**
    * Node end position from the beginning of the document.
    */
  def end: Int

  /**
    * Whether the node is selected.
    */
  def selected: Boolean

  /**
    * Node children.
    */
  def children: Seq[StructureNodeJSON]

  /**
    * Node category, if determined by a category filter.
    */
  def category: String
}
