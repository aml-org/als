package org.mulesoft.als.common.dtoTypes

case class ReferenceStack(stack: Seq[ReferenceOrigins]) {
  def through(reference: ReferenceOrigins) = ReferenceStack(reference +: stack)
}