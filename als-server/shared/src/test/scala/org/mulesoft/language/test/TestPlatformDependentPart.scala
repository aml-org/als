package org.mulesoft.language.test

import amf.client.remote.Content
import amf.core.unsafe.PlatformSecrets
import org.mulesoft.language.server.core.platform.PlatformDependentPart

import scala.concurrent.Future

class TestPlatformDependentPart extends PlatformDependentPart with PlatformSecrets {

  /**
    * Fetches contents via http
    *
    * @param url
    * @return
    */
  override def fetchHttp(url: String): Future[Content] = platform.resolve(url)

  /** encodes a complete uri. Not encodes chars like / */
  override def encodeURI(url: String): String = url

  /** encodes a uri component, including chars like / and : */
  override def encodeURIComponent(url: String): String = url

  /** decode a complete uri. */
  override def decodeURI(url: String): String = url

  /** decodes a uri component */
  override def decodeURIComponent(url: String): String = url

  override def normalizeURL(url: String): String = url

  override def operativeSystem(): String = ""
}

object TestPlatformDependentPart {
  def apply(): TestPlatformDependentPart = new TestPlatformDependentPart()
}
