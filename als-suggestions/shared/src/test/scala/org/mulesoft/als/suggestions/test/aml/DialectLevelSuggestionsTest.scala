package org.mulesoft.als.suggestions.test.aml

import org.mulesoft.als.suggestions.test.SuggestionsTest

trait DialectLevelSuggestionsTest extends SuggestionsTest {

  protected case class TestCaseLabel(label: String, dialectClass: Option[String], level: Int)

  protected case class TestCase(path: String, labels: Seq[TestCaseLabel])

  protected case class Result(succeed: Boolean, dialectClass: String, message: Option[String] = None)

  protected case class PositionResult(position: Int, dialectClass: Option[String], level: Int, content: String)

  private def buildPositions(content: String, cases: Seq[TestCaseLabel]): (String, Seq[PositionResult]) = {
    var partialContent = content
    val results = cases.map { c =>
      val info = this.findMarker(partialContent, c.label)
      partialContent = info.content
      PositionResult(info.offset, c.dialectClass, c.level, info.content)
    }
    (partialContent, results)
  }

  protected def assert(actualSet: Set[String], golden: Set[String]): (Boolean, Option[String]) = {
    val diff1 = actualSet.diff(golden)
    val diff2 = golden.diff(actualSet)

    diff1.foreach(println)
    diff2.foreach(println)

    if (diff1.isEmpty && diff2.isEmpty) (true, None)
    else
      (false, Some(s"Difference: got [${actualSet.mkString(", ")}] while expecting [${golden.mkString(", ")}]"))
  }

}
