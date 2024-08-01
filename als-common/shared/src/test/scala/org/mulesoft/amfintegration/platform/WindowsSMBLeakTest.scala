package org.mulesoft.amfintegration.platform

import org.scalatest.funsuite.AnyFunSuite

class WindowsSMBLeakTest extends AnyFunSuite {

  val matches: Set[String] = Set(
    "file:%5c%5ctest",
    "file:/%5c%5ctest",
    "file://%5c%5ctest",
    "file:///%5c%5ctest",
    "file:////%5c%5ctest",
    "file:\\\\test",
    "file:/\\\\test",
    "file://\\\\test",
    "file:///\\\\test",
    "\\\\test",
    "%5c%5ctest",
    "//test",
    "file:////test"
  )

  test("should not match URI without SMB leak") {
    assert(!WindowsLeakRegex("file:///test/correct"))
    assert(!WindowsLeakRegex("file://test/correct"))
    assert(!WindowsLeakRegex("file:/test/correct"))
    assert(!WindowsLeakRegex("file:test/correct"))
    assert(!WindowsLeakRegex("/test/correct"))
  }

  matches.foreach { resource =>
    test(s"should match $resource") {
      assert(WindowsLeakRegex.apply(resource))
    }
  }
}
