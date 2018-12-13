//package org.mulesoft.als.outline.structure.structureDefault
//
//
//
//class StructureDefaultIndex {
//
//  var _defaultDecorator = new DefaultDecorator()
//  var _defaultLabelProvider = new DefaultLabelProvider()
//
//  def addDecoration(nodeType: NodeType, decoration: Decoration): Unit = {
//    _defaultDecorator.addDecoration(nodeType, decoration)
//
//  }
//
//  def DefaultKeyProvider(node: IParseResult): String = {
//    if ((!node))
//      return null
//    if ((node && (!node.parent()))) {
//      return node.name()
//
//    }
//    else {
//      return ((node.name() + " :: ") + DefaultKeyProvider(node.parent()))
//
//    }
//
//  }
//
//  def DefaultVisibilityFilter(node: IParseResult): Boolean = {
//    return true
//
//  }
//
//  def initialize() = {
//    structureImpl.setKeyProvider(DefaultKeyProvider)
//    structureImpl.addLabelProvider(_defaultLabelProvider)
//    structureImpl.addDecorator(_defaultDecorator)
//    structureImpl.setVisibilityFilter(DefaultVisibilityFilter)
//
//  }
//
//
//}
