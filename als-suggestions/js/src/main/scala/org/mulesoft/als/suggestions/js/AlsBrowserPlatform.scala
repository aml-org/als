package org.mulesoft.als.suggestions.js

import amf.client.remote.Content
import amf.core.remote.File.FILE_PROTOCOL
import amf.core.remote.browser.JsBrowserPlatform
import amf.core.remote.{File, HttpParts}
import amf.internal.environment.Environment

import scala.concurrent.Future

class AlsBrowserPlatform(defaultEnvironment: Environment = Environment()) extends JsBrowserPlatform {
  def withDefaultEnvironment(defaultEnvironment: Environment): AlsBrowserPlatform = new AlsBrowserPlatform(defaultEnvironment)

  override def resolve(url: String, env: Environment = defaultEnvironment): Future[Content] = super.resolve(url, env)

  override def resolvePath(rawPath: String): String = {
    rawPath match {
      case File(path) =>
        if (path.startsWith("/")) FILE_PROTOCOL + normalizeURL(path)
        else FILE_PROTOCOL + normalizeURL(path).substring(1)

      case HttpParts(protocol, host, path) => protocol + host + normalizePath(path)
      case _ => rawPath
    }
  }
}
