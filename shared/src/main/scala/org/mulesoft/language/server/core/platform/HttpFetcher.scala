package org.mulesoft.language.server.core.platform

import amf.core.remote.Content

import scala.concurrent.Future

trait HttpFetcher {

  /**
    * Fetches contents via http
    * @param url
    * @return
    */
  def fetchHttp(url: String): Future[Content]
}
