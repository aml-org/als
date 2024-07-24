package org.mulesoft.als.server.textsync

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.mulesoft.lsp.Initializable

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TextDocumentContainer(private val uriToEditor: mutable.Map[String, TextDocument] = mutable.Map())
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

  override def getResourceLoader: ResourceLoader =
    staticResourceLoader

  override def openedFiles: Seq[String] = uriToEditor.keys.toSeq

  override def filesInMemory: Map[String, TextDocument] = uriToEditor.toMap

  override def initialize(): Future[Unit] = Future.successful()
}

trait EnvironmentProvider extends Initializable with AlsPlatformSecrets {
  def getResourceLoader: ResourceLoader
  def openedFiles: Seq[String]
  def filesInMemory: Map[String, TextDocument]
}
