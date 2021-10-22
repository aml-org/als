package org.mulesoft.amfintegration.dialect.dialects

import amf.core.client.common.remote.Content
import amf.core.client.scala.lexer.CharSequenceStream

trait RawInMemoryDialect {
  val name: String
  val yaml: String
  final lazy val uri: String      = s"file://als/dialects/memory/$name.yaml"
  final lazy val content: Content = Content(new CharSequenceStream(yaml), uri)
}
