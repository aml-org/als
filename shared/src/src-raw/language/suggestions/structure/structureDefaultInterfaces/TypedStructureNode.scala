package org.mulesoft.als.suggestions.structure.structureDefaultInterfaces

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.Decoration;
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.TypedStructureNode;

trait TypedStructureNode extends StructureInterfacesIndex.StructureNode {
  var `type`: NodeType
}
