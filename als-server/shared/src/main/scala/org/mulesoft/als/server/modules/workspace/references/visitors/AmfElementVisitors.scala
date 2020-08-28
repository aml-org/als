package org.mulesoft.als.server.modules.workspace.references.visitors

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfElement, AmfObject}
import org.mulesoft.als.actions.common.{AliasInfo, RelationshipLink}
import org.mulesoft.als.server.modules.workspace.references.visitors.aliases.{AliasesVisitor, AliasesVisitorType}
import org.mulesoft.als.server.modules.workspace.references.visitors.documentlink.{
  DocumentLinkVisitor,
  DocumentLinkVisitorType
}
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins._
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.reflect.ClassTag

object AmfElementDefaultVisitors {
  private val allVisitors: Seq[AmfElementVisitorFactory] = {
    Seq(
      TraitLinksVisitor,
      AbstractDefinitionLinksVisitor,
      RamlTypeExpressionsVisitor,
      ExternalNodeReferenceVisitor,
      DeclaredLinksVisitor,
      YNodeAliasVisitor,
      DocumentLinkVisitor,
      AliasesVisitor,
      AMLDialectVisitor
    )
  }
  def build(bu: BaseUnit): AmfElementVisitors =
    new AmfElementVisitors(allVisitors.flatMap { _(bu) })
}

class AmfElementVisitors(allVisitors: Seq[AmfElementVisitor[_]]) {
  private def collectVisitors[R, T <: AmfElementVisitor[R]: ClassTag]: Seq[R] = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    allVisitors
      .collect {
        case v: T if clazz.isInstance(v) => v
      }
      .flatMap(_.report)
  }

  final def applyAmfVisitors(elements: BaseUnit): Unit = {
    val iterator = AlsIteratorStrategy.iterator(elements)
    while (iterator.hasNext) {
      val element = iterator.next()
      allVisitors.foreach(_.visit(element))
    }
  }

  final def getRelationshipsFromVisitors: Seq[RelationshipLink] =
    collectVisitors[RelationshipLink, NodeRelationshipVisitorType]

  final def getAliasesFromVisitors: Seq[AliasInfo] =
    collectVisitors[AliasInfo, AliasesVisitorType]

  final def getDocumentLinksFromVisitors: Map[String, Seq[DocumentLink]] =
    collectVisitors[(String, Seq[DocumentLink]), DocumentLinkVisitorType]
      .groupBy(_._1)
      .mapValues(_.flatMap(_._2))
}
