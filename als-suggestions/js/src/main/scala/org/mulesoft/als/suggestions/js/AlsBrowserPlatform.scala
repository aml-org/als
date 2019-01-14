package org.mulesoft.als.suggestions.js

import amf.core.remote.File.FILE_PROTOCOL
import amf.core.remote.{File, HttpParts}
import amf.core.remote.browser.JsBrowserPlatform

object AlsBrowserPlatform extends JsBrowserPlatform {
  override def resolvePath(oath: String): String = {
    oath match {
      case File(path) =>
        if (path.startsWith("/")) FILE_PROTOCOL + normalizeURL(path)
        else FILE_PROTOCOL + normalizeURL(path).substring(1)

      case HttpParts(protocol, host, path) => protocol + host + normalizePath(path)
      case _ => oath
    }
  }
}
