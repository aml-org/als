package org.mulesoft.als.common

import java.net.{URI, URISyntaxException}

import amf.core.remote.Platform

object FileUtils {

  val FILE_PROTOCOL = amf.core.remote.File.FILE_PROTOCOL

  def getPath(iri: String, platform: Platform): String =
    if (iri.startsWith(FILE_PROTOCOL)) {
      val url =
        try new URI(iri)
        catch {
          case _: URISyntaxException => // Fallback, encode and try
            new URI(platform.encodeURI(iri))
        }
      Option(url.getHost).getOrElse("") + url.getPath
    } else iri

  def getEncodedUri(path: String, platform: Platform): String =
    platform.encodeURI(getDecodedUri(path, platform))

  def getDecodedUri(uri: String, platform: Platform): String =
    FILE_PROTOCOL + getPath(uri, platform)
}
