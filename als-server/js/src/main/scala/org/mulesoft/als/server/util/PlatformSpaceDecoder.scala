package org.mulesoft.als.server.util

object PlatformSpaceDecoder extends SpaceDecoder {
  override def decodeSpace(uri: String): String = uri

  override def encodeSpace(uri: String): String = uri
}
