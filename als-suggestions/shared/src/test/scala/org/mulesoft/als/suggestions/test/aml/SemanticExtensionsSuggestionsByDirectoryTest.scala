package org.mulesoft.als.suggestions.test.aml

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.remote.{Hint, VocabularyYamlHint}
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.suggestions.test.SuggestionByDirectoryTest
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
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
  override def preload(directory: String, amfConfiguration: AmfConfigurationWrapper): Future[Unit] = {
    val dir = Fs.syncFile(directory)
    if (dir.isDirectory) {
      val futureRegistrations = dir.list
        .filter(_.endsWith(semanticFileExtensions))
        .map(smxn => s"$directory${fs.separatorChar}$smxn".toAmfUri(platform))
        .map { extensionUri =>
          amfConfiguration
            .parse(extensionUri)
            .map { r =>
              r.result.baseUnit match {
                case d: Dialect =>
                  amfConfiguration.registerDialect(d)
                case _ =>
              }
            }
        }
      Future.sequence(futureRegistrations.toSeq).flatMap(_ => Future.unit)
    } else Future.unit
  }
}
