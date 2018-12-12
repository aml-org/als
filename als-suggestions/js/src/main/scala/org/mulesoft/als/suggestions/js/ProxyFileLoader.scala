package org.mulesoft.als.suggestions.js

import amf.core.remote.Platform
import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.internal.resource.ResourceLoader
import amf.core.remote._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProxyFileLoader(fsProvider: IFSProvider, platform: Platform)
  extends FileLoader(fsProvider, platform) {

  var overrideUrl: Option[String] = None
  var overrideContent: Option[String] = None

  def withOverride(url: String, content: String): Unit = {

    this.overrideUrl = Some(url)
    this.overrideContent = Some(content)
  }

  /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
  override def fetch(path: String): Future[Content] = {

    if (overrideUrl.isDefined && (path == overrideUrl.get || ("file://" + overrideUrl.get) == path)) {

      Future.successful(Content(new CharSequenceStream(path, overrideContent.get),
        platform.ensureFileAuthority(path),
        platform.extension(path).flatMap(platform.mimeFromExtension)))

    } else {

      super.fetch(path)
    }
  }

  /** Accepts specified resource. */
  override def accepts(resource: String): Boolean = {

    if (overrideUrl.isDefined && (resource == overrideUrl.get || ("file://" + overrideUrl.get) == resource)) {

      true
    } else {

      super.accepts(resource)
    }
  }
}
