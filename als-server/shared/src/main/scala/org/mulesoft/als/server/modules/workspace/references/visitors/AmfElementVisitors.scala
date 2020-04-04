package org.mulesoft.als.server.modules.workspace.references.visitors

import amf.core.model.domain.AmfElement
import amf.core.traversal.iterator.AmfElementStrategy
import org.mulesoft.als.actions.common.{AliasInfo, RelationshipLink}
import org.mulesoft.als.server.modules.workspace.references.visitors.aliases.{AliasesVisitor, AliasesVisitorType}
import org.mulesoft.als.server.modules.workspace.references.visitors.documentlink.{
  DocumentLinkVisitor,
  DocumentLinkVisitorType
}
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins.{
  DeclaredLinksVisitor,
  TraitLinksVisitor,
  YNodeAliasVisitor
}
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.reflect.ClassTag

object AmfElementDefaultVisitors {
  private val allVisitors: Seq[AmfElementVisitorFactory] = {
    Seq(TraitLinksVisitor, DeclaredLinksVisitor, YNodeAliasVisitor, DocumentLinkVisitor, AliasesVisitor)
  }
  def build(): AmfElementVisitors = {
    new AmfElementVisitors(allVisitors.map(_()))
  }
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

  final def applyAmfVisitors(elements: List[AmfElement]): Unit = {
    val iterator = AmfElementStrategy.iterator(elements)
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
