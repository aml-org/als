package common.dtoTypes

import org.scalatest.FunSuite

class PositionTest extends FunSuite {

  test("Greater than") {
    val reference = Position(2, 2)

    assert(!(reference > reference))
    assert(!(reference > Position(2, 3)))
    assert(!(reference > Position(3, 3)))
    assert(!(reference > Position(3, 2)))
    assert(!(reference > Position(3, 1)))

    assert(reference > Position(2, 1))
    assert(reference > Position(1, 1))
    assert(reference > Position(1, 2))
    assert(reference > Position(1, 3))
  }

  test("Less than") {
    val reference = Position(2, 2)

    assert(!(reference < reference))
    assert(!(reference < Position(2, 1)))
    assert(!(reference < Position(1, 1)))
    assert(!(reference < Position(1, 2)))
    assert(!(reference < Position(1, 3)))

    assert(reference < Position(2, 3))
    assert(reference < Position(3, 3))
    assert(reference < Position(3, 2))
    assert(reference < Position(3, 1))
  }

  test("Greater than or equal to") {
    val reference = Position(2, 2)

    assert(!(reference >= Position(2, 3)))
    assert(!(reference >= Position(3, 3)))
    assert(!(reference >= Position(3, 2)))
    assert(!(reference >= Position(3, 1)))

    assert(reference >= reference)
    assert(reference >= Position(2, 1))
    assert(reference >= Position(1, 1))
    assert(reference >= Position(1, 2))
    assert(reference >= Position(1, 3))
  }

  test("Less than or equal to") {
    val reference = Position(2, 2)

    assert(!(reference <= Position(2, 1)))
    assert(!(reference <= Position(1, 1)))
    assert(!(reference <= Position(1, 2)))
    assert(!(reference <= Position(1, 3)))

    assert(reference <= reference)
    assert(reference <= Position(2, 3))
    assert(reference <= Position(3, 3))
    assert(reference <= Position(3, 2))
    assert(reference <= Position(3, 1))
  }

}
