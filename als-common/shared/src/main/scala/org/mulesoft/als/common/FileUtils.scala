package org.mulesoft.als.common

import java.net.{URI, URISyntaxException}

import amf.core.remote.Platform

private object FileUtils {

  val FILE_PROTOCOL: String = amf.core.remote.File.FILE_PROTOCOL

  def isValidUri(uri: String): Boolean =
    try {
      new URI(uri)
      true
    } catch {
      case _: URISyntaxException => false
    }

  def getPath(iri: String, platform: Platform): String =
    if (iri.startsWith(FILE_PROTOCOL)) {
      val url =
        try new URI(iri)
        catch {
          case _: URISyntaxException => // Fallback, encode and try
            new URI(platform.encodeURI(iri))
        }
      Option(url.getHost).orElse(Option(url.getAuthority)).getOrElse("") + url.getPath
    } else iri

  def getEncodedUri(path: String, platform: Platform): String =
    platform.encodeURI(getDecodedUri(path, platform))

  def getDecodedUri(uri: String, platform: Platform): String =
    FILE_PROTOCOL + getPath(uri, platform)

  def getWithProtocol(uri: String): String = {
    if (uri.toUpperCase.startsWith("HTTP")) uri
    else {
      if (uri.toUpperCase.startsWith("FILE")) uri
      else FILE_PROTOCOL + uri
    }
  }
}
