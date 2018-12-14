// $COVERAGE-OFF$
package org.mulesoft.language.server.core.platform

import java.net.URI

import amf.client.remote.Content

import scala.concurrent.Future

trait PlatformDependentPart {

  /**
    * Fetches contents via http
    * @param url
    * @return
    */
  def fetchHttp(url: String): Future[Content]

  /** encodes a complete uri. Not encodes chars like / */
  def encodeURI(url: String): String

  /** encodes a uri component, including chars like / and : */
  def encodeURIComponent(url: String): String

  /** decode a complete uri. */
  def decodeURI(url: String): String

  /** decodes a uri component */
  def decodeURIComponent(url: String): String

  def normalizeURL(url: String): String

  def normalizePath(url: String): String = new URI(encodeURI(url)).normalize.toString

  def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] = stream.toString.find(p)
  
  def operativeSystem(): String;
}
// $COVERAGE-ON$