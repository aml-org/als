package org.mulesoft.als.server.textsync

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.Platform
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.lsp.Initializable

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TextDocumentContainer(override val amfConfiguration: AmfConfigurationWrapper,
                                 private val uriToEditor: mutable.Map[String, TextDocument] = mutable.Map())
    extends EnvironmentProvider {

  def +(tuple: (String, TextDocument)): TextDocumentContainer = {
    uriToEditor.put(tuple._1, tuple._2)
    this
  }

  def get(uri: String): Option[TextDocument] =
    uriToEditor.get(uri)

  def getContent(uri: String): String = get(uri).map(_.text).getOrElse("")

  def exists(uri: String): Boolean = get(uri).isDefined

  def uris: Set[String] = uriToEditor.keys.toSet

  def remove(uri: String): Unit =
    if (uriToEditor.contains(uri))
      uriToEditor.remove(uri)

  def versionOf(uri: String): Option[Int] = get(uri).map(_.version)

  private def mutableResourceLoader = {
    new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] =
        Future {
          new Content(getContent(resource), resource)
        }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean = exists(resource)
    }
  }

  private def staticResourceLoader =
    new ResourceLoader {
      private val current: Map[String, String] =
        uriToEditor.map(t => t._1 -> t._2.text).toMap

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] =
        Future {
          new Content(current(resource), resource)
        }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean =
        current.contains(resource)
    }

  override def amfConfigurationSnapshot(): AmfConfigurationWrapper = {
    val branch = amfConfiguration.branch
    branch.withResourceLoader(staticResourceLoader)
    branch
  }

  override def openedFiles: Seq[String] = uriToEditor.keys.toSeq

  override def initialize(): Future[Unit] = {
    amfConfiguration.withResourceLoader(mutableResourceLoader)
    Future.successful()
  }

  override def branch: EnvironmentProvider = TextDocumentContainer(amfConfigurationSnapshot(), uriToEditor)
}

trait EnvironmentProvider extends Initializable {
  def amfConfigurationSnapshot(): AmfConfigurationWrapper
  val amfConfiguration: AmfConfigurationWrapper
  def platform: Platform = amfConfiguration.platform
  def openedFiles: Seq[String]
  def branch: EnvironmentProvider
}
