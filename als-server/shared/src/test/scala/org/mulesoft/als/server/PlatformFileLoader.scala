package org.mulesoft.als.server

import amf.client.remote.Content
import amf.core.remote.Platform
import amf.internal.resource.ResourceLoader

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlatformFileLoader(private val platform: Platform) extends ResourceLoader {
  override def fetch(resource: String): Future[Content] =
    platform.fs
      .asyncFile(resource)
      .read()
      .map(content => new Content(content.toString, resource))

  override def accepts(resource: String): Boolean =
    resource
      .take(7)
      .toLowerCase
      .equals("file://")
}
