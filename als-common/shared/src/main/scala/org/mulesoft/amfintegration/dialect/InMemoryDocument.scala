package org.mulesoft.amfintegration.dialect

import amf.core.client.common.remote.Content
import amf.core.client.scala.lexer.CharSequenceStream

trait InMemoryDocument {
  val name: String
  val fileContent: String
  val extension: String
  def uri: String = s"file://als/dialects/memory/$name.$extension"
  final lazy val content: Content = Content(new CharSequenceStream(fileContent), uri)
}
