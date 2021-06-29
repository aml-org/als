package org.mulesoft.amfintegration.visitors

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.api.WebApi
import amf.core.client.scala.model.document.{BaseUnit, EncodesModel}
import amf.core.client.scala.model.domain.AmfElement

import scala.collection.mutable

trait AmfElementVisitor[R] extends Visitor[AmfElement, R]

trait AmfElementVisitorFactory {
  def apply(bu: BaseUnit): Option[AmfElementVisitor[_]]
  def applies(bu: BaseUnit): Boolean = true
}

trait DialectElementVisitorFactory extends AmfElementVisitorFactory {
  override final def applies(bu: BaseUnit): Boolean = bu.isInstanceOf[Dialect]
}

trait WebApiElementVisitorFactory extends AmfElementVisitorFactory {
  override final def applies(bu: BaseUnit): Boolean =
    bu match {
      case _: WebApi       => true
      case e: EncodesModel => e.encodes.isInstanceOf[WebApi]
      case _               => false
    }
}

trait Visitor[T, R] {

  private val results: mutable.ListBuffer[R] = mutable.ListBuffer()

  protected def innerVisit(element: T): Seq[R]

  final def visit(element: T): Unit =
    results ++= innerVisit(element)

  final def report: List[R] = {
    val r = results.toList
    results.clear()
    r
  }
}
