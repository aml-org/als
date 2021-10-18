package org.mulesoft.als.suggestions.test

import amf.core.internal.remote.Hint
import org.mulesoft.als.common.ByDirectoryTest
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.common.io.{Fs, SyncFile}
import org.scalatest.AsyncFreeSpec

import scala.concurrent.{ExecutionContext, Future}

trait SuggestionByDirectoryTest extends AsyncFreeSpec with BaseSuggestionsForTest with ByDirectoryTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  def basePath: String

  def dir: SyncFile = Fs.syncFile(basePath)

  def origin: Hint

  s"Suggestions test for vendor ${origin.spec.toString} by directory" - {
    forDirectory(dir, "")
  }

  def preload(parent: String, amfConfiguration: AmfConfigurationWrapper): Future[Unit] = Future.unit

  override def testFile(content: String, f: SyncFile, parent: String): Unit = {
    s"Suggest over ${f.name} at dir ${cleanDirectory(f)}" in {
      val expected = s"${f.parent}${platform.fs.separatorChar}expected${platform.fs.separatorChar}${f.name}.json"
      for {
        amfConfiguration <- defaultAmfConfiguration.map(_.branch)
        _                <- amfConfiguration.init()
        _                <- preload(f.path.stripSuffix(f.name), amfConfiguration)
        s <- suggestFromFile(
          content,
          "file://" + f.path.replaceAllLiterally(platform.fs.separatorChar.toString, "/"),
          "*",
          amfConfiguration,
          None
        )
        tmp <- writeTemporaryFile(expected)(
          writeDataToString(s.sortWith((s1, s2) => s1.label.compareTo(s2.label) < 0).toList))
        r <- assertDifferences(tmp, expected)
      } yield r
    }
  }

  private def cleanDirectory(f: SyncFile) =
    f.path.stripPrefix("als-suggestions/shared/src/test/resources/test/").stripSuffix(f.name)
}
