package org.mulesoft.als.configuration

import amf.client.convert.CoreClientConverters._
import amf.client.remote.Content
import amf.client.resource.{ClientResourceLoader, ResourceLoader}
import scala.concurrent.ExecutionContext.Implicits.global

object ResourceLoaderConverter {

  def internalResourceLoader(loader: ClientResourceLoader): amf.internal.resource.ResourceLoader = {
    val intermediary = new ResourceLoader {
      override def fetch(resource: String): ClientFuture[Content] = loader.fetch(resource)

      override def accepts(resource: String): Boolean = loader.accepts(resource)
    }
    ResourceLoaderMatcher.asInternal(intermediary)
  }
}
