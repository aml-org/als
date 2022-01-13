package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.textsync.{EnvironmentProvider, TextDocument}

import scala.concurrent.Future

object DummyEnvironmentProvider extends EnvironmentProvider {
  override def getResourceLoader: ResourceLoader = new ResourceLoader {
    override def fetch(resource: String): Future[Content] = Future.successful(new Content("", resource))
    override def accepts(resource: String): Boolean       = false
  }
  override def openedFiles: Seq[String]                 = Seq.empty
  override def filesInMemory: Map[String, TextDocument] = Map()
  override def initialize(): Future[Unit]               = Future.successful()
}