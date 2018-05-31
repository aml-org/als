package org.mulesoft.als.suggestions.js

import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import amf.internal.resource.ResourceLoader
import org.mulesoft.common.io.FileSystem

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ProxyContentPlatform(fsProvider: IFSProvider) extends FSProviderBasedPlatform(fsProvider) {

  override protected def createFileLoader(fsProvider: IFSProvider): ResourceLoader = {
    new ProxyFileLoader(fsProvider, this)
  }

  def withOverride(url: String, content: String): Unit = {

    fileLoader.asInstanceOf[ProxyFileLoader].withOverride(url, content)
  }

}
