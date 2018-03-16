package org.mulesoft.als.suggestions.structure.structureInterfaces

import org.mulesoft.als.suggestions.structure.raml_1_parser.Raml1ParserIndex
import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureNodeJSON;
import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureNode;
import org.mulesoft.als.suggestions.structure.structureInterfaces.ContentProvider;

trait StructureNodeJSON {
  var text: String
  var typeText: String
  var icon: String
  var textStyle: String
  var key: String
  var start: Int
  var end: Int
  var selected: Boolean
  var children: Array[StructureNodeJSON]
  var category: String
}
