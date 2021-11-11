package org.mulesoft.amfintegration.visitors

import amf.apicontract.client.scala.model.domain.{EndPoint, Operation}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.templates.AbstractDeclaration
import amf.core.client.scala.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.client.scala.traversal.iterator.AmfIterator
import amf.core.internal.annotations.ErrorDeclaration
import org.mulesoft.amfintegration.AbstractDeclarationInformation
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.amfconfiguration.AmfParseContext

import scala.collection.mutable

class AlsElementIterator(private val bu: BaseUnit,
                         private var buffer: Iterator[AmfElement],
                         visited: mutable.Set[String],
                         parseContext: AmfParseContext)
    extends AmfIterator {

  def this(bu: BaseUnit, parseContext: AmfParseContext) = {
    this(bu, Iterator(bu), mutable.Set(), parseContext)
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
            if de.linkTarget.exists(_ => de.effectiveLinkTarget().isInstanceOf[ErrorDeclaration[_]]) =>
          visited += de.id
          advance()
        case e: ErrorDeclaration[_] =>
          visited += e.id
        case abstractDeclaration: AbstractDeclaration =>
          val information =
            AbstractDeclarationInformation.extractInformation(abstractDeclaration, bu, parseContext.amfConfiguration)
          information.map(info => {
            info.element match {
              case obj @ (_: EndPoint | _: Operation) =>
                visited += info.original.id
                buffer = (obj :: extractElements(obj).toList ++ buffer).iterator
              case _ => None
            }
          })
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
