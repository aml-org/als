package org.mulesoft.als.common

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.ResourceNotFound
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AmfConfigurationPatcher {

  def patch(configuration: AmfConfigurationWrapper, uri: String, content: String): AmfConfigurationWrapper = {
    val patchLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future(new Content(content, resource))

      override def accepts(resource: String): Boolean = resource == uri
    }

    configuration.withResourceLoader(patchLoader)
    configuration
  }

  def patch(configuration: AmfConfigurationWrapper, files: Map[String, String]): AmfConfigurationWrapper = {
    val patchLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = files.get(resource) match {
        case Some(content) => Future(new Content(content, resource))
        case None          => Future.failed(new ResourceNotFound(s"Resource $resource not found"))
      }

      override def accepts(resource: String): Boolean = files.contains(resource)
    }

    configuration.withResourceLoader(patchLoader)
    configuration
  }

}
