package org.mulesoft.lsp.feature.link

import org.mulesoft.exceptions.PathTweaks
import org.mulesoft.lsp.feature.common.Range

/** A document link is a range in a text document that links to an internal or external resource, like another text
  * document or a web site.
  *
  * @range:
  *   The range this link applies to.
  * @target:
  *   The uri this link points to. If missing a resolve request is sent later.
  * @data:
  *   A data entry field that is preserved on a document link between a DocumentLinkRequest and a
  *   DocumentLinkResolveRequest.
  */
case class DocumentLink(range: Range, target: String, data: Option[Any] = None)

object DocumentLink {
  def apply(range: Range, target: String, data: Option[Any]): DocumentLink =
    new DocumentLink(range, PathTweaks(target), data)
}
