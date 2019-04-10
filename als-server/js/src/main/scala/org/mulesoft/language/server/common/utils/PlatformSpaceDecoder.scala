package org.mulesoft.language.server.common.utils

// $COVERAGE-OFF$

object PlatformSpaceDecoder extends SpaceDecoder {
  def decodeSpace(uri: String): String = uri
  def encodeSpace(uri: String): String = uri
}

// $COVERAGE-ON$