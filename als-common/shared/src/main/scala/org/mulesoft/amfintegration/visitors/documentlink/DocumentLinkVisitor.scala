package org.mulesoft.amfintegration.visitors.documentlink

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfElement
import org.mulesoft.amfintegration.visitors.AmfElementVisitorFactory
import org.mulesoft.amfintegration.visitors.links.FindLinks
import org.mulesoft.lsp.feature.link.DocumentLink

class DocumentLinkVisitor extends DocumentLinkVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[(String, Seq[DocumentLink])] = {
    element match {
      case bu: BaseUnit =>
        Seq((bu.location().getOrElse(bu.id), FindLinks.getLinks(bu)))
      case _ => Nil
    }
  }
}

object DocumentLinkVisitor extends AmfElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[DocumentLinkVisitor] = Some(new DocumentLinkVisitor())
}
