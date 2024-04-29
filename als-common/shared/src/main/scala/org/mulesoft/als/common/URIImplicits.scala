package org.mulesoft.als.common

import amf.core.internal.remote.Platform

object URIImplicits {
  implicit class StringUriImplicits(uri: String) {
    def relativize(to: String): String = {
      var uriSplit = uri.split('/')
      val toSplit  = to.split('/').iterator
      uriSplit = uriSplit.dropWhile { part =>
        if (toSplit.hasNext && toSplit.next.contains(part)) {
          toSplit.drop(1)
          uriSplit.nonEmpty
        } else false
      }
      toSplit.foreach(_ => uriSplit = ".." +: uriSplit)
      uriSplit.mkString("/")
    }

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
