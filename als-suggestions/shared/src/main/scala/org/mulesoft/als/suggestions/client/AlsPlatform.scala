package org.mulesoft.als.suggestions.client

import amf.client.remote.Content
import amf.core.remote.File.FILE_PROTOCOL
import amf.core.remote.{File, HttpParts, Platform}
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader

import scala.concurrent.Future

abstract class AlsPlatform(val defaultEnvironment: Environment = Environment()) extends Platform {
  override def resolve(url: String, env: Environment = defaultEnvironment): Future[Content] = {
    super.resolve(url, env)
  }

  override def resolvePath(rawPath: String): String = {
    rawPath match {
      case File(path) =>
        if (path.startsWith("/")) FILE_PROTOCOL + normalizeURL(path).substring(1)
        else FILE_PROTOCOL + normalizeURL(path)

      case HttpParts(protocol, host, path) => protocol + host + normalizePath(path)
      case _ => rawPath
    }
  }

  def withOverride(url: String, content: String): AlsPlatform = {
    val resolvedPath = resolvePath(url)

    val loader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, resource))

      override def accepts(resource: String): Boolean = resource == resolvedPath
    }

    withDefaultEnvironment(defaultEnvironment.withLoaders(loader +: defaultEnvironment.loaders))
  }

  def withDefaultEnvironment(defaultEnvironment: Environment): AlsPlatform
}