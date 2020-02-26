package org.mulesoft.als.server.modules.workspace.references.visitors.aliases

import amf.core.model.domain.AmfElement
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory

class AliasesVisitor extends AliasesVisitorType {
  override protected def innerVisit(element: AmfElement): Option[Result] = None
//        getAliasesAnnotation(element)

// where does this belong?
//  private def getAliasesAnnotation(element: AmfElement) = {
//    element match {
//      case bu: BaseUnit =>
//        val tuples = FindLinks.getAliases(bu)
//        tuples.headOption // TODO: FIX ME
//      case _ => None
//    }
}

object AliasesVisitor extends AmfElementVisitorFactory {
  override def apply(): AliasesVisitor = new AliasesVisitor()
}
