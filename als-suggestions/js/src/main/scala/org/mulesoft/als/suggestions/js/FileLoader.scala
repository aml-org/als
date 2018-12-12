package org.mulesoft.als.suggestions.js

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.internal.resource.ResourceLoader
import amf.core.remote._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileLoader(protected val fsProvider: IFSProvider, protected val platform: Platform)
  extends ResourceLoader {

  /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
  def fetch(path: String): Future[Content] = {

    val uri = if(path.startsWith(File.FILE_PROTOCOL)) path else File.FILE_PROTOCOL + path


    this.fsProvider.contentAsync(uri).toFuture
      .map(
        content => {
          Content(new CharSequenceStream(path, content),
            platform.ensureFileAuthority(path),
            platform.extension(path).flatMap(platform.mimeFromExtension))
        })
  }

  /** Accepts specified resource. */
  def accepts(resource: String): Boolean = {

    !(resource.startsWith("http") || resource.startsWith("HTTP"))
  }

}
