package org.mulesoft.als.common.dtoTypes

import org.scalatest.funsuite.AnyFunSuite

class PositionTest extends AnyFunSuite {

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

  test("offset to Position") {
    val text = "Lorem ipsum dolor\n\n sit amet, consectetur adip\n\niscing elit, sed \n\n"

    assert(Position(0, text) == Position(0, 0))
    assert(Position(1, text) == Position(0, 1))
    assert(Position(17, text) == Position(0, 17))
    assert(Position(18, text) == Position(1, 0))
    assert(Position(46, text) == Position(2, 27))
    assert(Position(66, text) == Position(5, 0))
    assert(Position(67, text) == Position(6, 0))
  }

  test("Position to offset") {
    val text = "Lorem ipsum dolor\n\n sit amet, consectetur adip\n\niscing elit, sed \n\n"

    assert(Position(0, 0).offset(text) == 0)
    assert(Position(0, 1).offset(text) == 1)
    assert(Position(0, 17).offset(text) == 17)
    assert(Position(1, 0).offset(text) == 18)
    assert(Position(2, 27).offset(text) == 46)
    assert(Position(5, 0).offset(text) == 66)
    assert(Position(6, 0).offset(text) == 67)
  }

  test("Position to offset and back") {
    val text = "Lorem ipsum dolor\n\n sit amet, consectetur adip\n\niscing elit, sed \n\n"

    assert(Position(Position(0, 0).offset(text), text) == Position(0, 0))
    assert(Position(Position(0, 1).offset(text), text) == Position(0, 1))
    assert(Position(Position(0, 17).offset(text), text) == Position(0, 17))
    assert(Position(Position(1, 0).offset(text), text) == Position(1, 0))
    assert(Position(Position(2, 27).offset(text), text) == Position(2, 27))
    assert(Position(Position(5, 0).offset(text), text) == Position(5, 0))
    assert(Position(Position(6, 0).offset(text), text) == Position(6, 0))
  }

  test("offset to Position and back") {
    val text = "Lorem ipsum dolor\n\n sit amet, consectetur adip\n\niscing elit, sed \n\n"

    assert(Position(0, text).offset(text) == 0)
    assert(Position(1, text).offset(text) == 1)
    assert(Position(17, text).offset(text) == 17)
    assert(Position(18, text).offset(text) == 18)
    assert(Position(46, text).offset(text) == 46)
    assert(Position(66, text).offset(text) == 66)
    assert(Position(67, text).offset(text) == 67)
  }

}
