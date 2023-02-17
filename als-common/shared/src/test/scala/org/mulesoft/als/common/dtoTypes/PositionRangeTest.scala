package org.mulesoft.als.common.dtoTypes

import org.scalatest.funsuite.AnyFunSuite

class PositionRangeTest extends AnyFunSuite {

  test("Includes in one line range") {
    val range = PositionRange(Position(2, 2), Position(2, 3))

    assert(range.contains(Position(2, 2)))
    assert(range.contains(Position(2, 3)))

    assert(!range.contains(Position(2, 1)))
    assert(!range.contains(Position(2, 4)))

    assert(!range.contains(Position(1, 1)))
    assert(!range.contains(Position(1, 2)))
    assert(!range.contains(Position(1, 3)))
    assert(!range.contains(Position(1, 4)))

    assert(!range.contains(Position(3, 1)))
    assert(!range.contains(Position(3, 2)))
    assert(!range.contains(Position(3, 3)))
    assert(!range.contains(Position(3, 4)))

  }
  test("Includes in multi line range") {
    val range = PositionRange(Position(2, 2), Position(3, 3))

    assert(range.contains(Position(2, 2)))
    assert(range.contains(Position(2, 3)))
    assert(range.contains(Position(2, 4)))
    assert(range.contains(Position(2, 5)))

    assert(range.contains(Position(3, 0)))
    assert(range.contains(Position(3, 1)))
    assert(range.contains(Position(3, 2)))
    assert(range.contains(Position(3, 3)))

    assert(!range.contains(Position(2, 0)))
    assert(!range.contains(Position(2, 1)))
    assert(!range.contains(Position(3, 4)))
    assert(!range.contains(Position(3, 5)))

    assert(!range.contains(Position(1, 1)))
    assert(!range.contains(Position(1, 2)))
    assert(!range.contains(Position(1, 3)))
    assert(!range.contains(Position(1, 4)))

    assert(!range.contains(Position(4, 1)))
    assert(!range.contains(Position(4, 2)))
    assert(!range.contains(Position(4, 3)))
    assert(!range.contains(Position(4, 4)))
  }
}
