package org.mulesoft.high.level.implementation

import amf.client.remote.Content
import amf.core.remote.{Context, Platform}
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.high.level.interfaces.DirectoryResolver

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

abstract class AlsPlatform(val defaultEnvironment: Environment = Environment()) extends Platform {
  override def resolve(url: String, env: Environment = defaultEnvironment): Future[Content] =
    super.resolve(url, env)

  def withOverride(url: String, content: String): AlsPlatform = {
    val resolvedPath = resolvePath(url)

    val loader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, resource))

      override def accepts(resource: String): Boolean = resource == resolvedPath
    }

    withDefaultEnvironment(defaultEnvironment.withLoaders(loader +: defaultEnvironment.loaders))
  }

  def withDefaultEnvironment(defaultEnvironment: Environment): AlsPlatform

  private val resolver: DirectoryResolver = new DirectoryResolver {

    override def exists(path: String): Future[Boolean] = fs.asyncFile(refinePath(path)).exists

    override def readDir(path: String): Future[Seq[String]] =
      fs.asyncFile(refinePath(path)).list.map(array => array.toSeq)

    override def isDirectory(path: String): Future[Boolean] = fs.asyncFile(refinePath(path)).isDirectory

    override protected def refinePath(path: String): String = {
      var p = path
      if (p.startsWith("file://"))
        if (operativeSystem().toLowerCase().startsWith("win")
            && p.startsWith("file:///"))
          p = p.substring("file:///".length)
        else
          p = p.substring("file://".length)
      p
    }
  }

  def directoryResolver: DirectoryResolver = resolver

  def resolvePath(absBasePath: String, path: String): Option[String] =
    Option(decodeURI(Context(this, normalizePath(absBasePath)).resolve(normalizePath(path))))
}

object AlsPlatform {
  def default = new AlsPlatformWrapper(dirResolver = None)
}
