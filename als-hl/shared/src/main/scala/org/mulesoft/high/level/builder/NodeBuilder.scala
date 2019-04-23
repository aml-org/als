package org.mulesoft.high.level.builder

import amf.core.model.document.{BaseUnit, Document, Fragment}
import amf.core.model.domain.AmfObject
import amf.plugins.document.webapi.model.NamedExampleFragment
import amf.plugins.domain.shapes.models.Examples
import org.mulesoft.high.level.implementation.ASTNodeImpl
import org.mulesoft.typesystem.project.ITypeCollectionBundle

object NodeBuilder {

  def buildAST(baseUnit: BaseUnit, bundle: ITypeCollectionBundle, factory: IASTFactory): Option[ASTNodeImpl] = {

    var result: Option[ASTNodeImpl] = None
    val nominalType                 = factory.determineUserType(baseUnit, None, None, bundle)
    factory.determineRootType(baseUnit, nominalType) match {
      case Some(rootType) =>
        val node = ASTNodeImpl(determineRootNode(baseUnit), baseUnit, None, rootType, None)
        nominalType.foreach(node.setLocalType)
        fillChildren(node, factory, bundle)
        result = Some(node)
      case _ => //throw new Error("Unable to determine root type")
    }
    result

  }

  def determineRootNode(baseUnit: BaseUnit): AmfObject = {
    baseUnit match {
      case fragment: NamedExampleFragment => fragment
      case fragment: Fragment             => fragment.encodes
      case document: Document             => document.encodes
      case _                              => baseUnit
    }
  }

  def fillChildren(node: ASTNodeImpl, factory: IASTFactory, bundle: ITypeCollectionBundle): Unit = {
    node.definition.allProperties.foreach(
      factory
        .getPropertyValues(node, _, bundle)
        .foreach(x => {
          if (x.isElement) fillChildren(x.asElement.get, factory, bundle)
          node.addChild(x)
        }))
  }

}
