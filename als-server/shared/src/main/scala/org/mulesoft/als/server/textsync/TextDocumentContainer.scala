package org.mulesoft.als.server.textsync

import amf.client.remote.Content
import amf.core.remote.Platform
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.Initializable

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TextDocumentContainer(givenEnvironment: Environment,
                                 override val platform: Platform,
                                 override val amfConfiguration: AmfInstance,
                                 private val uriToEditor: mutable.Map[String, TextDocument] = mutable.Map())
    extends EnvironmentProvider
    with Initializable {

  def patchUri(uri: String, patchedContent: TextDocument): TextDocumentContainer = {
    val copiedMap = uriToEditor.clone()
    copiedMap.update(uri, patchedContent)
    this.copy(uriToEditor = copiedMap)
  }

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

  val environment: Environment = givenEnvironment
    .add(new ResourceLoader {

      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] =
        Future { new Content(getContent(resource), resource) }

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean = exists(resource)
    })

  override def environmentSnapshot(): Environment =
    givenEnvironment
      .add(new ResourceLoader {
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
      })

  override def openedFiles: Seq[String] = uriToEditor.keys.toSeq
}

trait EnvironmentProvider extends Initializable {
  def environmentSnapshot(): Environment
  val amfConfiguration: AmfInstance
  val platform: Platform
  def openedFiles: Seq[String]

  override def initialize(): Future[Unit] = amfConfiguration.init()
}
