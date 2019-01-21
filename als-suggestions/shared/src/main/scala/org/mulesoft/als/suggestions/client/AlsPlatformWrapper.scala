package org.mulesoft.als.suggestions.client

import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.common.io.FileSystem

class AlsPlatformWrapper(defaultEnvironment: Environment = Environment()) extends AlsPlatform(defaultEnvironment) with PlatformSecrets {
  override def withDefaultEnvironment(defaultEnvironment: Environment): AlsPlatform = new AlsPlatformWrapper(defaultEnvironment)

  override def findCharInCharSequence(s: CharSequence)(p: Char => Boolean): Option[Char] = platform.findCharInCharSequence(s)(p)

  override val fs: FileSystem = platform.fs

  override def loaders(): Seq[ResourceLoader] = platform.loaders()

  override def encodeURI(url: String): String = platform.encodeURI(url)

  override def decodeURI(url: String): String = platform.decodeURI(url)

  override def encodeURIComponent(url: String): String = platform.encodeURIComponent(url)

  override def decodeURIComponent(url: String): String = platform.decodeURIComponent(url)

  override def normalizeURL(url: String): String = platform.normalizeURL(url)

  override def normalizePath(url: String): String = platform.normalizePath(url)

  override def tmpdir(): String = platform.tmpdir()

  override def operativeSystem(): String = platform.operativeSystem()
}
