package org.mulesoft.als.suggestions.test.aml

import amf.core.internal.remote.{Hint, VocabularyYamlHint}
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.common.io.Fs

import scala.concurrent.Future

class SemanticExtensionsSuggestionsByDirectoryTest extends SuggestionByDirectoryTest {
  override def basePath: String            = "als-suggestions/shared/src/test/resources/test/AML/semantic-extensions"
  override def fileExtensions: Seq[String] = Seq(".yaml", ".raml")
  override def ignoredFiles: Seq[String]   = Seq(".ignore", "smx")
  def semanticFileExtensions: String       = ".smx"

  override def origin: Hint = VocabularyYamlHint // why is origin necessary?

  /**
    * register each semantic extension
    * @param directory
    * @return
    */
  override def preload(directory: String, editorConfiguration: EditorConfiguration): Future[Unit] = {
    val dir = Fs.syncFile(directory)
    if (dir.isDirectory) {
      Future {
        dir.list
          .filter(_.endsWith(semanticFileExtensions))
          .map(smxn => s"$directory${fs.separatorChar}$smxn".toAmfUri(platform))
          .foreach { extensionUri =>
            editorConfiguration
              .withDialect(extensionUri)
          }
      }
    } else Future.unit
  }
}
