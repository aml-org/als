package org.mulesoft.als.common

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AmfConfigurationPatcher {

  def patch(configuration: ALSConfigurationState, uri: String, content: String): ALSConfigurationState = {
    val patchLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future(new Content(content, resource))

      override def accepts(resource: String): Boolean = resource == uri
    }
    ALSConfigurationState(configuration.editorState, configuration.projectState, Some(patchLoader))
  }

  def resourceLoaderForFile(fileUrl: String, content: String): ResourceLoader = new ResourceLoader {
    override def accepts(resource: String): Boolean = resource == fileUrl

    override def fetch(resource: String): Future[Content] =
      Future.successful(new Content(content, fileUrl))
  }

}
