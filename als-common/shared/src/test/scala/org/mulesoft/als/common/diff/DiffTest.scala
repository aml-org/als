package org.mulesoft.als.common.diff

import org.scalatest.FunSuite
import org.scalatest.Matchers._

/** */
class DiffTest extends FunSuite with ListAssertions {

  test("Case Insensitive Diff") {
    val deltas = Diff.caseInsensitive.diff(first, second)

    val out2 = "1,3c1\n" +
      "<   The Way that can be told of is not the eternal Way;\n" +
      "<     The name that can be named is not the eternal name.\n" +
      "<         The Nameless is the origin of Heaven and Earth;\n" +
      "---\n" +
      ">     The Nameless is the origin of Heaven and Earth;\n" +
      "4a3\n" +
      "> \n" +
      "11a11,13\n" +
      ">     They both may be called deep and profound.\n" +
      ">     Deeper and more profound\n" +
      ">     The door of all subtleties!\n"

    assertResult(out2) {
      Diff.makeString(deltas)
    }

    deltas.head.toString should startWith("Diff.Delta(0, c, 0, (  The")
  }

  test("Case Insensitive Diff Strings By Line") {
    val deltas: List[Diff.Delta[String]] = Diff.caseInsensitive.diff("Hello\nWorld", "HELLO\nWORLD")
    deltas shouldBe empty
  }

  test("Case Sensitive Diff") {
    val deltas: List[Diff.Delta[String]] = Diff.caseSensitive.diff(first, second)
    val out1: String = "1,4c1,3\n" +
      "<   The Way that can be told of is not the eternal Way;\n" +
      "<     The name that can be named is not the eternal name.\n" +
      "<         The Nameless is the origin of Heaven and Earth;\n" +
      "<     The Named is the mother of all things.\n" +
      "---\n" +
      ">     The Nameless is the origin of Heaven and Earth;\n" +
      ">     The named is the mother of all things.\n" +
      "> \n" +
      "11a11,13\n" +
      ">     They both may be called deep and profound.\n" +
      ">     Deeper and more profound\n" +
      ">     The door of all subtleties!\n"
    Diff.makeString(deltas) shouldEqual out1
  }

  test("Diff Strings By Line") {
    val deltas: List[Diff.Delta[String]] = Diff.caseSensitive.diff("Hello\nWorld", "")
    deltas.size shouldEqual 1

    deltas.head.toString shouldEqual "Diff.Delta(0, d, 0, (Hello, World), ())"
  }

  // ~ Static Fields ................................................................................................................................

  val first = List(
    "  The Way that can be told of is not the eternal Way;",
    "    The name that can be named is not the eternal name.",
    "        The Nameless is the origin of Heaven and Earth;",
    "    The Named is the mother of all things.",
    "    Therefore let there always be non-being,",
    "      so we may see their subtlety,",
    "    And let there always be being,",
    "      so we may see their outcome.",
    "    The two are the same,",
    "    But after they are produced,",
    "      they have different names."
  )

  val second = List(
    "    The Nameless is the origin of Heaven and Earth;",
    "    The named is the mother of all things.",
    "",
    "    Therefore let there always be non-being,",
    "      so we may see their subtlety,",
    "    And let there always be being,",
    "      so we may see their outcome.",
    "    The two are the same,",
    "    But after they are produced,",
    "      they have different names.",
    "    They both may be called deep and profound.",
    "    Deeper and more profound",
    "    The door of all subtleties!"
  )
}
