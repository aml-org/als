package org.mulesoft.als.server.modules.ast

import amf.client.remote.Content
import amf.client.resource.ResourceNotFound
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.server.textsync.TextDocumentManager

import scala.concurrent.Future

case class TextDocumentLoader(textDocumentManager: TextDocumentManager) extends ResourceLoader {
  override def fetch(resource: String): Future[Content] = {
    textDocumentManager
      .getTextDocument(resource)
      .map(document => document.buffer.getText()) match {
      case Some(content: String) => Future.successful(new Content(content, resource))
      case None                  => Future.failed(new ResourceNotFound(s"Resource $resource not found in server"))
    }
  }

  override def accepts(resource: String): Boolean =
    resource
      .take(7)
      .toLowerCase
      .equals("file://")
}
