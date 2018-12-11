package org.mulesoft.high.level.builder

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, Shape}
import amf.core.remote.Vendor
import org.mulesoft.high.level.implementation.BasicASTNode
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}
import org.mulesoft.typesystem.nominal_interfaces.{IProperty, ITypeDefinition, IUniverse}
import org.mulesoft.typesystem.project.ITypeCollectionBundle

import scala.concurrent.Future

trait IASTFactory {
    def getPropertyValues(node:IHighLevelNode, amfNode:AmfObject, doc:BaseUnit, prop:IProperty, bundle:ITypeCollectionBundle):Seq[BasicASTNode]

    def getPropertyValues(node:IHighLevelNode, prop:IProperty, bundle:ITypeCollectionBundle):Seq[BasicASTNode]

    def discriminate(clazz:ITypeDefinition,amfNode:AmfObject, nominalType:Option[ITypeDefinition]):ITypeDefinition = clazz

    def discriminateShape(shape:Shape,universe:IUniverse):Option[ITypeDefinition]

    def builtinSuperType(shape:Shape):Option[ITypeDefinition]

    def determineRootType(baseUnit: BaseUnit, nominalType:Option[ITypeDefinition]): Option[ITypeDefinition]

    def determineUserType(amfNode:AmfObject,nodeProperty:Option[IProperty],parent:Option[IHighLevelNode], _bundle:ITypeCollectionBundle):Option[ITypeDefinition]

    def builtInFacetValue(name:String,shape:Shape):Option[Any]

    def format:Vendor

    def newChild(node:IHighLevelNode, prop:IProperty, typeHint:Option[ITypeDefinition] = None):Option[IParseResult]

    protected def init():Future[Unit]
}
