package org.mulesoft.als.common

import amf.core.internal.remote.Platform

import java.net.{URI, URISyntaxException}
private object FileUtils {

  val FILE_PROTOCOL: String = amf.core.internal.remote.File.FILE_PROTOCOL

  def isValidUri(uri: String): Boolean =
    try {
      new URI(uri)
      true
    } catch {
      case _: URISyntaxException => false
    }

  def getPath(iri: String, platform: Platform): String = {
    val path = if (iri.startsWith(FILE_PROTOCOL)) {
      val url =
        try new URI(iri)
        catch {
          case _: URISyntaxException => // Fallback, encode and try
            new URI(platform.encodeURI(iri))
        }
      Option(url.getHost).orElse(Option(url.getAuthority)).getOrElse("") + url.getPath
    } else iri
    windowsPatchToPath(path, platform)
  }

  def getEncodedUri(path: String, platform: Platform): String =
    platform.encodeURI(getDecodedUri(path, platform))

  def getDecodedUri(uri: String, platform: Platform): String =
    FILE_PROTOCOL + windowsPatchToAbsoluteUri(getPath(uri, platform), platform)

  /**
    * if the path starts with drive letter in windows, add a `/` at the start
    * @param path
    * @param platform
    * @return
    */
  private def windowsPatchToAbsoluteUri(path: String, platform: Platform): String =
    if (platform.operativeSystem() == "win")
      s"/$path"
    else path

  /**
    * if the path starts with `/` and it is Windows, drop the slash
    * @param path
    * @param platform
    * @return
    */
  private def windowsPatchToPath(path: String, platform: Platform): String =
    if (platform.operativeSystem() == "win" && path.startsWith("/")) path.drop(1) else path // need to check if drive letter is defined? (see TmpResourceLoader)
}
