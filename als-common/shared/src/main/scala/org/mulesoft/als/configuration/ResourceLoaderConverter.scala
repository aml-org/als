package org.mulesoft.als.configuration

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.{ClientResourceLoader, ResourceLoader}
import amf.core.client.scala.resource
import amf.core.internal.convert.CoreClientConverters._

import scala.concurrent.ExecutionContext.Implicits.global

object ResourceLoaderConverter {

  def internalResourceLoader(loader: ClientResourceLoader): resource.ResourceLoader = {
    val intermediary: ResourceLoader = new ResourceLoader {
      override def fetch(resource: String): ClientFuture[Content] = loader.fetch(resource)

      override def accepts(resource: String): Boolean = loader.accepts(resource)
    }
    ResourceLoaderMatcher.asInternal(intermediary)
  }
}
