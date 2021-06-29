package org.mulesoft.als.suggestions.test

import amf.core.internal.remote.Hint
import org.mulesoft.als.common.ByDirectoryTest
import org.mulesoft.common.io.{Fs, SyncFile}
import org.scalatest.AsyncFreeSpec

import scala.concurrent.ExecutionContext

trait SuggestionByDirectoryTest extends AsyncFreeSpec with BaseSuggestionsForTest with ByDirectoryTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  def basePath: String

  def dir: SyncFile = Fs.syncFile(basePath)

  def origin: Hint

  s"Suggestions test for vendor ${origin.spec.toString} by directory" - {
    forDirectory(dir, "")
  }

  override def testFile(content: String, f: SyncFile, parent: String): Unit = {
    s"Suggest over ${f.name} at dir $parent${dir.name}" in {
      val expected = f.parent + platform.fs.separatorChar + "expected" + platform.fs.separatorChar + f.name + ".json"
      for {
        s <- suggestFromFile(
          content,
          "file://" + f.path.replaceAllLiterally(platform.fs.separatorChar.toString, "/"),
          "*",
          None
        )
        tmp <- writeTemporaryFile(expected)(
          writeDataToString(s.sortWith((s1, s2) => s1.label.compareTo(s2.label) < 0).toList))
        r <- assertDifferences(tmp, expected)
      } yield r
    }
  }
}
