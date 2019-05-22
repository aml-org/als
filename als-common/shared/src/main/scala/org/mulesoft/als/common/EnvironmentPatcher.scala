package org.mulesoft.als.common

import amf.client.remote.Content
import amf.client.resource.ResourceNotFound
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader

import scala.concurrent.Future

object EnvironmentPatcher {

  def patch(environment: Environment, uri: String, content: String): Environment = {
    val patchLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, resource))

      override def accepts(resource: String): Boolean = resource == uri
    }

    environment.withLoaders(patchLoader +: environment.loaders)
  }

  def patch(environment: Environment, files: Map[String, String]): Environment = {
    val patchLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = files.get(resource) match {
        case Some(content) => Future.successful(new Content(content, resource))
        case None          => Future.failed(new ResourceNotFound(s"Resource $resource not found"))
      }

      override def accepts(resource: String): Boolean = files.contains(resource)
    }

    environment.withLoaders(patchLoader +: environment.loaders)
  }

}
