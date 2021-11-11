package org.mulesoft.als.suggestions.test

import amf.core.internal.remote.Hint
import org.mulesoft.als.common.ByDirectoryTest
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
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

  def preload(parent: String, amfConfiguration: EditorConfiguration): Future[Unit] = Future.unit

  override def testFile(content: String, f: SyncFile, parent: String): Unit = {
    s"Suggest over ${f.name} at dir ${cleanDirectory(f)}" in {
      val expected = s"${f.parent}${platform.fs.separatorChar}expected${platform.fs.separatorChar}${f.name}.json"
      for {
        editorConfiguration <- Future(EditorConfiguration())
        _                   <- preload(f.path.stripSuffix(f.name), editorConfiguration)
        alsConfigurationState <- editorConfiguration.getState.map(
          ALSConfigurationState(_, EmptyProjectConfigurationState(), None))
        s <- suggestFromFile(
          content,
          "file://" + f.path.replaceAllLiterally(platform.fs.separatorChar.toString, "/"),
          "*",
          alsConfigurationState
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
