package org.mulesoft.amfintegration.platform

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformBuilder
import org.mulesoft.als.logger.Logger
import org.mulesoft.common.io.FileSystem

import scala.concurrent.{ExecutionContext, Future}
trait AlsPlatformSecrets {
  private val internalPlatform: Platform = PlatformBuilder()

  val platform: Platform = new Platform {
    override val globalExecutionContext: ExecutionContext = internalPlatform.globalExecutionContext

    private def overrideLoaders(implicit executionContext: ExecutionContext): Seq[ResourceLoader] =
      internalPlatform.loaders().map(SecuredLoader)

    override def name: String = internalPlatform.name

    override def findCharInCharSequence(s: CharSequence)(p: Char => Boolean): Option[Char] =
      internalPlatform.findCharInCharSequence(s)(p)

    override val fs: FileSystem = internalPlatform.fs

    override def loaders()(implicit executionContext: ExecutionContext): Seq[ResourceLoader] = overrideLoaders

    override def encodeURI(url: String): String = internalPlatform.encodeURI(url)

    override def decodeURI(url: String): String = internalPlatform.decodeURI(url)

    override def encodeURIComponent(url: String): String = internalPlatform.encodeURIComponent(url)

    override def decodeURIComponent(url: String): String = internalPlatform.decodeURIComponent(url)

    override def tmpdir(): String = internalPlatform.tmpdir

    override def operativeSystem(): String = internalPlatform.operativeSystem

    // no idea where this is used, but it is overriden in JvmPlatform and I am afraid to change it
    override protected def customValidationLibraryHelperLocation: String =
      if (name == "jvm") "classpath:validations/amf_validation.js" else super.customValidationLibraryHelperLocation
  }

  private case class SecuredLoader(loader: ResourceLoader) extends ResourceLoader {
    override def fetch(resource: String): Future[Content] =
      loader.fetch(resource)

    override def accepts(resource: String): Boolean =
      !potentialLeak(resource) && loader.accepts(resource)

    private def potentialLeak(resource: String): Boolean =
      WindowsLeakRegex(resource.toLowerCase)
  }
}

protected object WindowsLeakRegex {
  private def checkForLiteral(literal: String, prefix: String)(implicit resource: String): Boolean = {
    val regex = s"$prefix/*$literal".r
    regex.findPrefixMatchOf(resource).nonEmpty
  }

  private def doubleCheckLiteral(literal: String)(implicit resource: String): Boolean =
    checkForLiteral(literal, "") || checkForLiteral(literal, filePrefix)
  def apply(implicit resource: String): Boolean =
    doubleCheckLiteral(windowsSMB) || doubleCheckLiteral(windowsSMBEncoded) || checkForLiteral(
      unixStyle,
      ""
    ) || checkForLiteral(unixStyle, s"$filePrefix//")
}
