package org.mulesoft.als.suggestions.test.aml

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.suggestions.test.SuggestionsTest
import org.scalatest.Assertion

import scala.concurrent.Future

trait SuggestionsWithDialectTest extends SuggestionsTest with FileAssertionTest {

  def runTest(file: String, dialect: String): Future[Assertion] =
    withDialect(file, s"expected/$file.json", dialect)

  /**
    * @param path                URI for the API resource
    * @param goldenPath          URI for the golden file
    * @param label               Pointer placeholder
    * @param cut                 if true, cuts text after label
    * @param labels              set of every label in the file (needed for cleaning API)
    */
  def runSuggestionTestWithDialect(path: String,
                                   goldenPath: String,
                                   label: String = "*",
                                   cut: Boolean = false,
                                   labels: Array[String] = Array("*"),
                                   dialect: Option[String] = None): Future[Assertion] =
    for {
      s <- suggest(path, label, dialect, defaultAmfConfiguration.branch)
      tmp <- writeTemporaryFile(goldenPath)(
        writeDataToString(s.sortWith((s1, s2) => s1.label.compareTo(s2.label) < 0).toList))
      r <- assertDifferences(tmp, goldenPath)
    } yield r

  def withDialect(path: String, goldenPath: String, dialectPath: String): Future[Assertion] = {
    runSuggestionTestWithDialect(path, filePath(goldenPath), dialect = Some(filePath(dialectPath)))
  }

}
