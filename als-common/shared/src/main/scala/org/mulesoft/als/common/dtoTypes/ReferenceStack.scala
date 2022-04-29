package org.mulesoft.als.common.dtoTypes

case class ReferenceStack(stack: Seq[ReferenceOrigins]) {
  def through(reference: Seq[ReferenceOrigins]): ReferenceStack = ReferenceStack(reference ++ stack)
}
