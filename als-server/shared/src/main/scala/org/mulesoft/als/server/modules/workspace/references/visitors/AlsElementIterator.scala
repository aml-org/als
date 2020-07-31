package org.mulesoft.als.server.modules.workspace.references.visitors

import amf.core.annotations.ErrorDeclaration
import amf.core.model.document.BaseUnit
import amf.core.model.domain.templates.AbstractDeclaration
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.traversal.iterator.AmfIterator
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import org.mulesoft.amfintegration.AmfImplicits._

import scala.collection.mutable

class AlsElementIterator(private val bu: BaseUnit,
                         private var buffer: Iterator[AmfElement],
                         visited: mutable.Set[String])
    extends AmfIterator {

  def this(bu: BaseUnit) = {
    this(bu, Iterator(bu), mutable.Set())
    advance()
  }

  override def hasNext: Boolean = buffer.hasNext

  override def next: AmfElement = {
    val current = buffer.next()
    advance()
    current
  }

  @scala.annotation.tailrec
  private def advance(): Unit =
    if (buffer.hasNext) {
      val current = buffer.next()
      current match {
        case obj: AmfObject if visited.contains(obj.id) =>
          advance()
        case de: AbstractDeclaration
            if de.linkTarget.exists(_ => de.effectiveLinkTarget().isInstanceOf[ErrorDeclaration]) =>
          visited += de.id
          advance()
        case rt: ResourceType =>
          val obj = rt.asEndpoint(bu)
          visited += rt.id
          buffer = (obj :: extractElements(obj).toList ++ buffer).iterator
        case t: Trait =>
          val obj = t.asOperation(bu)
          visited += t.id
          buffer = (obj :: extractElements(obj).toList ++ buffer).iterator
        case a: AmfObject if current.annotations.isRamlTypeExpression =>
          // don't search for children, the whole expression will be treated as a single element
          visited += a.id
          buffer = (a :: buffer.toList).iterator
        case obj: AmfObject =>
          visited += obj.id
          buffer = (obj :: extractElements(obj).toList ++ buffer).iterator
        case arr: AmfArray =>
          buffer = (arr :: arr.values.toList ++ buffer).iterator
        case _ =>
          buffer = (current :: buffer.toList).iterator
      }
    }

  private def extractElements(obj: AmfObject) =
    obj.fields.fields().map(_.element)
}
