package org.mulesoft.als.configuration

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class MaxSizeResourceLoader(delegate: ResourceLoader, maxSize: Int) extends ResourceLoader {

  override def fetch(resource: String): Future[Content] = delegate
    .fetch(resource)
    .map {
      case c if c.stream.toString.length > maxSize =>
        throw MaxSizeException(c.url, maxSize)
      case c =>
        c
    }

  override def accepts(resource: String): Boolean = delegate.accepts(resource)
}
