package org.mulesoft.als.common.diff

import org.scalatest.Assertions
import org.scalatest.compatible.Assertion

/** */
trait ListAssertions extends Assertions {

  def assert[E](actual: List[E], expected: List[E]): Assertion = {
    if (actual.size == expected.size) {
      actual.zipWithIndex.foreach { case (a, index) =>
        val e = expected(index)
        if (a != e) {
          fail(s"$a did not equal $e at index $index")
        }
      }
    } else fail(s"$actual did not contain the same elements that \n$expected")
    succeed
  }
}
