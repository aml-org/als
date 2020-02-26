package org.mulesoft.als.server.modules.workspace.references.visitors

import amf.core.model.domain.AmfElement

import scala.collection.mutable

trait AmfElementVisitor[R] extends Visitor[AmfElement, R] {
  type Result = R
}

trait AmfElementVisitorFactory {
  def apply(): AmfElementVisitor[_]
}

trait Visitor[T, R] {

  private val results: mutable.ListBuffer[Option[R]] = mutable.ListBuffer()

  protected def innerVisit(element: T): Option[R]

  final def visit(element: T): Unit =
    results.append(innerVisit(element))

  final def report: List[R] = results.toList.flatten
}
