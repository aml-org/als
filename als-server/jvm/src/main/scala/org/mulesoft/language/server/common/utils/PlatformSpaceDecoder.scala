package org.mulesoft.language.server.common.utils

import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets

// $COVERAGE-OFF$

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

// $COVERAGE-ON$
