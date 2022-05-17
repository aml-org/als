package org.mulesoft.als.common.dtoTypes

object AscendingPositionOrdering extends Ordering[Position] {
  override def compare(x: Position, y: Position): Int =
    if (x == y) 0 else if (x > y) 1 else -1
}

object DescendingPositionOrdering extends Ordering[Position] {
  override def compare(x: Position, y: Position): Int =
    if (x == y) 0 else if (x > y) 1 else -1
}
