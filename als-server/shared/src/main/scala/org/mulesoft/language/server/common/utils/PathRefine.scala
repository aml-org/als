// $COVERAGE-OFF$
package org.mulesoft.language.server.common.utils

import amf.core.remote.Platform

object PathRefine {

  def refinePath(uri: String, platform: Platform): String = {

    val isWindows = platform.operativeSystem().toLowerCase().indexOf("win") == 0
    // println(s"Platform is: ${platform.operativeSystem()}, windows detected: ${isWindows}")
    val decoded = SpaceDecoderBuilder.getDecoder.decodeSpace(uri)
    val result =
      if (isWindows && Option(decoded).isDefined)
        platform.decodeURIComponent(decoded).replace("\\", "/")
      else decoded

    if (!result.startsWith("file://") && !result.startsWith("http:") && !result.startsWith("https:"))
      if (result.startsWith("/"))
        "file://" + result
      else
        "file:///" + result
    else result
  }

  def encodePath(uri: String): String = {
    SpaceDecoderBuilder.getDecoder.encodeSpace(uri)
  }

}

object SpaceDecoderBuilder {
  def getDecoder: SpaceDecoder = PlatformSpaceDecoder
}

trait SpaceDecoder {
  def decodeSpace(uri: String): String
  def encodeSpace(uri: String): String
}

// $COVERAGE-ON$
