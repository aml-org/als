package org.mulesoft.als.configuration

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class MaxSizeResourceLoader(delegate: ResourceLoader, counter: MaxSizeCounter) extends ResourceLoader {

  override def fetch(resource: String): Future[Content] = delegate
    .fetch(resource)
    .map {
      case c if !counter.sum(c.stream.toString.length) =>
        throw MaxSizeException(c.url, counter.maxSize)
      case c =>
        c
    }

  override def accepts(resource: String): Boolean = delegate.accepts(resource)
}
