package org.mulesoft.als.suggestions.test

import amf.core.remote.Hint
import amf.core.unsafe.PlatformSecrets
import common.diff.FileAssertionTest
import org.mulesoft.als.suggestions.client.Suggestion
import org.mulesoft.als.suggestions.test.SuggestionNode._
import org.mulesoft.common.io.{Fs, SyncFile}
import org.scalatest.{Assertion, AsyncFreeSpec, AsyncFunSuite}
import upickle.default.write

import scala.concurrent.{ExecutionContext, Future}

trait SuggestionByDirectoryTest extends AsyncFreeSpec with BaseSuggestionsForTest with FileAssertionTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  def basePath: String

  def dir: SyncFile = Fs.syncFile(basePath)

  def origin: Hint

  def fileExtension: String

  s"Suggestions test for vendor ${origin.vendor.toString} by directory" - {
    forDirectory(dir)
  }

  private def forDirectory(dir: SyncFile): Unit = {
    val (subDirs, files) =
      dir.list
        .filterNot(_ == "expected")
        .map(l => Fs.syncFile(dir.path + fs.separatorChar + l))
        .partition(_.isDirectory)
    val validFiles = files.filter(f => f.name.endsWith(fileExtension) || f.name.endsWith(fileExtension + ".ignore"))
    if (subDirs.nonEmpty || validFiles.nonEmpty) {
      s"in directory: ${dir.name}" - {
        subDirs.foreach(forDirectory)
        validFiles.foreach { f =>
          val content = f.read()
          if (content.toString.contains("*")) {
            if (f.name.endsWith(".ignore")) s"Golden: ${f.name}" ignore {
              Future.successful(succeed)
            } else {
              s"Suggest over ${f.name}" in {
                testSuggestion(content.toString, f)
              }
            }
          } else Future.successful(succeed)

        }
      }
    }
  }

  def writeDataToString(data: List[Suggestion]): String =
    write[List[SuggestionNode]](data.map(SuggestionNode.sharedToTransport), 2)

  private def testSuggestion(content: String, f: SyncFile): Future[Assertion] = {
    val expected = f.parent + platform.fs.separatorChar + "expected" + platform.fs.separatorChar + f.name + ".json"
    for {
      s <- suggestFromFile(content,
                           "file://" + f.path.replaceAllLiterally(platform.fs.separatorChar.toString, "/"),
                           Some("application/" + origin.syntax.extension),
                           origin.vendor.toString,
                           None)
      tmp <- writeTemporaryFile(expected)(
        writeDataToString(s.sortWith((s1, s2) => s1.text.compareTo(s2.text) < 0).toList))
      r <- assertDifferences(tmp, expected)
    } yield r
  }
}
