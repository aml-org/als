package org.mulesoft.als.suggestions.structure.structureDefaultInterfaces

import org.mulesoft.als.suggestions.structure.structureInterfaces.StructureInterfacesIndex
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.Decoration;
import org.mulesoft.als.suggestions.structure.structureDefaultInterfaces.TypedStructureNode;

class StructureDefaultInterfacesIndex{

sealed abstract class NodeType
object NodeType {
   case object ATTRIBUTE extends NodeType
  case object RESOURCE extends NodeType
  case object METHOD extends NodeType
  case object SECURITY_SCHEME extends NodeType
  case object TYPE_DECLARATION extends NodeType
  case object ANNOTATION_DECLARATION extends NodeType
  case object DOCUMENTATION_ITEM extends NodeType
  case object EXTERNAL_UNIT extends NodeType
  case object UNKNOWN extends NodeType
  case object OTHER extends NodeType 
}
def isTypedStructureNode(node: StructureNode): Boolean = {
 return (node.asInstanceOf[Any]).`type`
 
}


}
