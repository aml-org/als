package org.mulesoft.als.server.workspace

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader

import scala.concurrent.Future

trait MockResourceLoader {

  case class MockFile(uri: String, content: String)

  def buildResourceLoaderForFile(mockFile: MockFile): ResourceLoader = {
    val fileUrl: String = mockFile.uri
    val content: String = mockFile.content
    new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    }
  }

}
