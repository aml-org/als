package org.mulesoft.als.common

import amf.core.remote.Platform

object URIImplicits {
  implicit class StringUriImplicits(uri: String) {
    def isValidUri: Boolean     = FileUtils.isValidUri(uri)
    def isValidFileUri: Boolean = uri.isValidUri && uri.toUpperCase.startsWith("FILE:")
    def toAmfUri(implicit platform: Platform): String =
      FileUtils.getEncodedUri(FileUtils.getPath(uri, platform), platform)
    def toAmfDecodedUri(implicit platform: Platform): String =
      FileUtils.getDecodedUri(FileUtils.getPath(uri, platform), platform)
    def toPath(implicit platform: Platform): String =
      FileUtils.getPath(uri, platform)
  }
}
