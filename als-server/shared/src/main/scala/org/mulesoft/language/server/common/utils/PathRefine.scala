// $COVERAGE-OFF$
package org.mulesoft.language.server.common.utils

import amf.core.remote.Platform

object PathRefine {

  def refinePath(uri: String, platform: Platform): String = {

    val isWindows = platform.operativeSystem().toLowerCase().indexOf("win") == 0
    // println(s"Platform is: ${platform.operativeSystem()}, windows detected: ${isWindows}")
    var result = uri
    if (isWindows) {
      if (Option(uri).isDefined) {
        result = platform.decodeURIComponent(uri).replace("\\", "/")
      }
    }
    if (!result.startsWith("file://") && !result.startsWith("http:") && !result.startsWith("https:")) {
      if (result.startsWith("/")) {
        result = "file://" + result
      }
      else {
        result = "file:///" + result
      }
    }
    result
  }

}

// $COVERAGE-ON$