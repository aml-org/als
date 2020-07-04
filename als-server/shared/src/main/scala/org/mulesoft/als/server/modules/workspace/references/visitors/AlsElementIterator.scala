package org.mulesoft.als.server.modules.workspace.references.visitors

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.traversal.iterator.AmfIterator
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.{ErrorResourceType, ErrorTrait}
import amf.plugins.domain.webapi.models.templates.{ParametrizedResourceType, ParametrizedTrait, ResourceType, Trait}
import amf.plugins.domain.webapi.models.{EndPoint, Operation}

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
        case rt: ResourceType =>
          val obj = rt.asEndpoint(bu)
          visited += rt.id
          buffer = (obj :: extractElements(obj).toList ++ buffer).iterator // todo: remove `extractElements`??
        case t: Trait =>
          val obj = t.asOperation(bu)
          visited += t.id
          buffer = (obj :: extractElements(obj).toList ++ buffer).iterator // todo: remove `extractElements`??
        case obj: AmfObject =>
          visited += obj.id
          buffer = (obj :: extractElements(obj).toList ++ buffer).iterator
        case arr: AmfArray =>
          buffer = (arr :: arr.values.toList ++ buffer).iterator
        case o =>
          buffer = (o :: buffer.toList).iterator
      }
    }

  private def extractElements(obj: AmfObject) =
    obj.fields.fields().map(_.element)
}
