package org.mulesoft.high.level.builder

import amf.core.model.document.{BaseUnit, Document, Fragment}
import amf.core.model.domain.AmfObject
import org.mulesoft.high.level.implementation.ASTNodeImpl
import org.mulesoft.typesystem.project.ITypeCollectionBundle

object NodeBuilder {

    def buildAST(
        baseUnit: BaseUnit,
        bundle:ITypeCollectionBundle,
        factory: IASTFactory): Option[ASTNodeImpl] = {

        var result: Option[ASTNodeImpl] = None
        factory.determineRootType(baseUnit) match {
            case Some(rootType) =>
                determineRootNode(baseUnit) match {
                    case Some(rootNode) =>
                        val node = ASTNodeImpl(rootNode, baseUnit, None, rootType, None)
                        factory.determineUserType(baseUnit,node,None,bundle).foreach(node.setLocalType)
                        fillChildren(node, factory,bundle)
                        result = Some(node)
                    case _ => throw new Error("Unable to determine root node")
                }
            case _ => //throw new Error("Unable to determine root type")
        }
        result

    }

    def determineRootNode(baseUnit:BaseUnit):Option[AmfObject] = {
        baseUnit match {
            case fragment:Fragment => Option(fragment.encodes)
            case document:Document => Option(document.encodes)
            case _ => Some(baseUnit)
        }
    }

    def fillChildren(node:ASTNodeImpl,factory:IASTFactory,bundle:ITypeCollectionBundle):Unit = {
        node.definition.allProperties.foreach(factory.getPropertyValues(node, _,bundle).foreach(x => {
            if (x.isElement) fillChildren(x.asElement.get, factory, bundle)
            node.addChild(x)
        }))
    }

}
