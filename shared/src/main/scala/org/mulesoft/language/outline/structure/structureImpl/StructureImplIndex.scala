//package org.mulesoft.als.outline.structure.structureImpl
//
//import org.mulesoft.als.outline.structure.structureInterfaces.StructureInterfacesIndex
//import org.mulesoft.als.outline.structure.raml_1_parser.Raml1ParserIndex
//import org.mulesoft.als.outline.structure.underscore.UnderscoreIndex
//import org.mulesoft.als.outline.structure..
//
//..common.commonInterfaces.CommonInterfacesIndex
//
//import org.mulesoft.als.outline.structure..
//
//..common.tools.ToolsIndex
//
//import org.mulesoft.als.outline.structure.structureImpl.StructureNodeImpl;
//
//class StructureImplIndex {
//
//  var _labelProviders: Array[LabelProvider] = Array()
//  var _decorators: Array[Decorator] = Array()
//  var _categoryFilters: {def apply(categoryName: String): CategoryFilter
//    /* def update() -- if you need it */
//  } = Map(
//  )
//  var _visibilityFilter: VisibilityFilter = null
//  var _contentProvider: ContentProvider = null
//  var _keyProvider: KeyProvider = null
//
//  def addLabelProvider(provider: LabelProvider) = {
//    _labelProviders.push(provider)
//
//  }
//
//  def addDecorator(decorator: Decorator) = {
//    _decorators.push(decorator)
//
//  }
//
//  def addCategoryFilter(categoryName: String, categoryFilter: CategoryFilter) = {
//    (_categoryFilters(categoryName) = categoryFilter)
//
//  }
//
//  def setVisibilityFilter(visibilityFilter: VisibilityFilter) = {
//    (_visibilityFilter = visibilityFilter)
//
//  }
//
//  def setContentProvider(contentProvider: ContentProvider) = {
//    (_contentProvider = contentProvider)
//
//  }
//
//  def setKeyProvider(keyProvider: KeyProvider) = {
//    (_keyProvider = keyProvider)
//
//  }
//
//  def isStructureNodeImpl(node: StructureNode): Boolean = {
//    return ((node.asInstanceOf[Any]).getSource != null)
//
//  }
//
//  def getLabelProvider(node: StructureNode): LabelProvider = {
//    if ((!_labelProviders))
//      return null
//    var source = node.getSource()
//    if ((!source))
//      return null
//    return _underscore_.find(_labelProviders, (labelProvider => (labelProvider.getLabelText(source) != null)))
//
//  }
//
//  def getDecorator(node: StructureNode): Decorator = {
//    if ((!_decorators))
//      return null
//    var source = node.getSource()
//    if ((!source))
//      return null
//    return _underscore_.find(_decorators, (decorator => {
//      return ((decorator.getIcon(source) != null) || (decorator.getTextStyle(source) != null))
//
//    }))
//
//  }
//
//
//
//  def cloneNode(toClone: StructureNode): StructureNode = {
//    var result: StructureNodeImpl = new StructureNodeImpl(toClone.getSource())
//    (result.text = toClone.text)
//    (result.typeText = toClone.typeText)
//    (result.icon = toClone.icon)
//    (result.textStyle = toClone.textStyle)
//    (result.children = toClone.children.asInstanceOf[Any])
//    (result.key = toClone.key)
//    (result.start = toClone.start)
//    (result.end = toClone.end)
//    (result.selected = toClone.selected)
//    (result.category = toClone.category)
//    return result
//
//  }
//
//  def filterTreeByCategory(root: StructureNode, categoryName: String): StructureNode = {
//    if ((!root.children))
//      return
//    var result = cloneNode(root)
//    var filteredChildren = root.children
//    if (categoryName) {
//      var filter = _categoryFilters(categoryName)
//      if (filter) {
//        (filteredChildren = _underscore_.filter(root.children, (child => filter(child.getSource()))))
//        filteredChildren.forEach((filteredChild => (filteredChild.category = categoryName)))
//
//      }
//
//    }
//    (result.children = filteredChildren)
//    return result
//
//  }
//
//  def buildTreeRecursively(structureNode: StructureNode, contentProvider: ContentProvider) = {
//    var children = contentProvider(structureNode)
//    if (children) {
//      (structureNode.children = children)
//      children.forEach((child => buildTreeRecursively(child, contentProvider)))
//
//    }
//    else {
//      (structureNode.children = Array())
//
//    }
//
//  }
//
//  var _selected: IParseResult = null
//
//  def defaultContentProvider(node: StructureNode): Array[StructureNode] = {
//    if ((node === null)) {
//      return Array()
//
//    }
//    var isStructureImpl = isStructureNodeImpl(node)
//    if ((!isStructureImpl))
//      return
//    var source: IParseResult = node.getSource()
//    if ((source == null))
//      return Array()
//    if (source.isAttr()) {
//      return Array()
//
//    }
//    if (source.isUnknown()) {
//      return Array()
//
//    }
//    var sourceChildren = source.children()
//    var filteredSourceChildren = sourceChildren.filter((child => ((!child.isAttr()) && (!child.isUnknown()))))
//    var result = Array()
//    filteredSourceChildren.forEach((child => {
//      if ((_visibilityFilter && (!_visibilityFilter(child))))
//        return
//      var converted = hlNodeToStructureNode(child, _selected)
//      if ((!converted))
//        return
//      result.push(converted)
//
//    }))
//    return result
//
//  }
//
//  def getStructure(categoryName: String): StructureNode = {
//    var hlRoot = tools.getRootNode()
//    if ((!hlRoot))
//      return null
//    var _selected = tools.getCurrentNode()
//    var structureRoot = hlNodeToStructureNode(hlRoot, _selected)
//    if ((!structureRoot))
//      return null
//    var contentProvider = _contentProvider
//    if ((!contentProvider))
//      (contentProvider = defaultContentProvider)
//    buildTreeRecursively(structureRoot, contentProvider)
//    var result = filterTreeByCategory(structureRoot, categoryName)
//    return result
//
//  }
//
//  def getStructureForAllCategories(): {def apply(categoryName: String): StructureNode
//    /* def update() -- if you need it */
//  } = {
//    var hlRoot = tools.getRootNode()
//    if ((!hlRoot))
//      return null
//    var _selected = tools.getCurrentNode()
//    var structureRoot = hlNodeToStructureNode(hlRoot, _selected)
//    if ((!structureRoot))
//      return null
//    var contentProvider = _contentProvider
//    if ((!contentProvider))
//      (contentProvider = defaultContentProvider)
//    buildTreeRecursively(structureRoot, contentProvider)
//    var result: {def apply(categoryName: String): StructureNode
//      /* def update() -- if you need it */
//    } = Map(
//    )
//    (_categoryFilters).keys.foreach { fresh1 =>
//      var categoryName = zeroOfMyType
//        = fresh1 {
//        if (_categoryFilters.`hasOwnProperty`(categoryName)) {
//          var filteredTree = filterTreeByCategory(structureRoot, categoryName)
//          (result(categoryName) = filteredTree)
//
//        }
//
//      }
//    }
//    return result
//
//  }
//
//
//}
