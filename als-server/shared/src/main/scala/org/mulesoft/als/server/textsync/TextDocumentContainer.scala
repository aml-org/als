package org.mulesoft.als.server.textsync

import amf.core.remote.Platform
import org.mulesoft.als.common.FileUtils

import scala.collection.mutable

case class TextDocumentContainer(platform: Platform,
                                 private val uriToEditor: mutable.Map[String, TextDocument] = mutable.Map()) {

  def patchUri(uri: String, patchedContent: TextDocument): TextDocumentContainer = {
    val copiedMap = uriToEditor.clone()
    copiedMap.update(uri, patchedContent)
    this.copy(uriToEditor = copiedMap)
  }

  def +(tuple: (String, TextDocument)): TextDocumentContainer = {
    uriToEditor.put(FileUtils.getPath(tuple._1, platform), tuple._2)
    this
  }

  def get(uri: String): Option[TextDocument] =
    uriToEditor.get(FileUtils.getPath(uri, platform))

  def getContent(uri: String): String = get(uri).map(_.text).getOrElse("")

  def exists(uri: String): Boolean = get(uri).isDefined

  def uris: Set[String] = uriToEditor.keys.toSet

  def remove(uri: String): Unit = {
    val path = FileUtils.getPath(uri, platform)
    if (uriToEditor.contains(path))
      uriToEditor.remove(path)
  }

  def versionOf(uri: String): Option[Int] = get(uri).map(_.version)
}
