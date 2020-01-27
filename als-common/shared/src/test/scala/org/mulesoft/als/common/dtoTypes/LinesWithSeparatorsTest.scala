package org.mulesoft.als.common.dtoTypes

import org.scalatest.{FunSuite, Matchers}

class LinesWithSeparatorsTest extends FunSuite with Matchers {

  test("Test end with whitespaces line") {

    val text = "ab\nfg\n  "

    val lines = TextHelper.linesWithSeparators(text)
    lines.length should be(3)
    lines.last should be("  ")
  }

  test("Test end with eol") {

    val text = "ab\nfg\n"

    val lines = TextHelper.linesWithSeparators(text)
    lines.length should be(3)
    lines.last.isEmpty should be(true)
  }

  test("Test empty lines in the middle") {

    val text = "ab\n\n\nfg\n3"

    val lines = TextHelper.linesWithSeparators(text)
    lines.length should be(5)
    lines.last should be("3")
  }

}
