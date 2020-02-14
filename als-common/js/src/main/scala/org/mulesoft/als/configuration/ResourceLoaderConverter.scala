package org.mulesoft.als.configuration

import amf.client.remote.Content
import amf.client.resource.ClientResourceLoader

import scala.concurrent.Future

object ResourceLoaderConverter {

  def internalResourceLoader(loader: ClientResourceLoader): amf.internal.resource.ResourceLoader =
    new amf.internal.resource.ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        loader.fetch(resource).toFuture

      override def accepts(resource: String): Boolean = loader.accepts(resource)
    }
}
