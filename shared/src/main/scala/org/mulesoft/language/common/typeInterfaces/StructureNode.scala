package org.mulesoft.language.common.typeInterfaces

case class StructureNode (
 /**
   * Node label text to be displayed.
   */
 text: String,
 /**
   * Node type label, if any.
   */
 typeText: Option[String],
 /**
   * Node icon. Structure module is not setting up, how icons are represented in the client
   * system, or what icons exist,
   * instead the client is responsible to configure the mapping from nodes to icon identifiers.
   */
 icon: String,
 /**
   * Text style of the node. Structure module is not setting up, how text styles are represented in the client
   * system, or what text styles exist,
   * instead the client is responsible to configure the mapping from nodes to text styles identifiers.
   */
 textStyle: String,
 /**
   * Unique node identifier.
   */
 key: String,
 /**
   * Node start position from the beginning of the document.
   */
 start: Int,
 /**
   * Node end position from the beginning of the document.
   */
 end: Int,
 /**
   * Whether the node is selected.
   */
 selected: Boolean,
 /**
   * Node children.
   */
 children: Seq[StructureNode],
 /**
   * Node category, if determined by a category filter.
   */
 category: String
)
{

}