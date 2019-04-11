package org.mulesoft.als.server.util

import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets

object PlatformSpaceDecoder extends SpaceDecoder {
  override def decodeSpace(uri: String): String =
    try java.net.URLDecoder.decode(uri, StandardCharsets.UTF_8.name)
    catch {
      case _: UnsupportedEncodingException => uri
    }

  override def encodeSpace(uri: String): String =
    try java.net.URLEncoder.encode(uri, StandardCharsets.UTF_8.name)
    catch {
      case _: UnsupportedEncodingException => uri
    }
}
