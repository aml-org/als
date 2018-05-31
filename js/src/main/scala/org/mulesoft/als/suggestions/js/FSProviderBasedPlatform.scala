package org.mulesoft.als.suggestions.js

import java.net.URI

import amf.client.remote.Content
import amf.core.lexer.CharSequenceStream
import amf.core.remote._
import amf.core.remote.server.Path
import amf.internal.resource.ResourceLoader
import org.mulesoft.common.io.FileSystem
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.URIUtils
import amf.internal.environment.Environment

object Http {
  def unapply(uri: String): Option[(String, String, String)] = uri match {
    case url if url.startsWith("http://") || url.startsWith("https://") =>
      val protocol        = url.substring(0, url.indexOf("://") + 3)
      val rightOfProtocol = url.stripPrefix(protocol)
      val host =
        if (rightOfProtocol.contains("/")) rightOfProtocol.substring(0, rightOfProtocol.indexOf("/"))
        else rightOfProtocol
      val path = rightOfProtocol.replace(host, "")
      Some(protocol, host, path)
    case _ => None
  }
}

object File {
  val FILE_PROTOCOL = "file://"

  def unapply(url: String): Option[String] = {
    url match {
      case s if s.startsWith(FILE_PROTOCOL) =>
        val path = s.stripPrefix(FILE_PROTOCOL)
        Some(path)
      case _ => None
    }
  }
}

/**
  * Platform based on external fs provider
  */
class FSProviderBasedPlatform (fsProvider: IFSProvider) extends Platform {

  override val fs: FileSystem = new FSProviderBasedFS(fsProvider)

  val fileLoader = this.createFileLoader(this.fsProvider)
  val loaders: Seq[ResourceLoader] = Seq(

    fileLoader,
    this.createHttpLoader()
  )

  val defaultEnvironment = new Environment(this.loaders)

  override def resolvePath(uri: String): String = {

    uri match {
      case File(path) =>
        if (path.startsWith("/")) {
          File.FILE_PROTOCOL + path
        } else {
          File.FILE_PROTOCOL + withTrailingSlash(path).substring(1)
        }

      case Http(protocol, host, path) => protocol + host + withTrailingSlash(path)
    }
  }



  override def resolve(url: String, environment: Environment = defaultEnvironment): Future[Content] =
    super.resolve(url, environment )

//  private def loaderConcat(url: String, loaders: Seq[ResourceLoader]): Future[Content] = loaders.toList match {
//    case Nil         => Future.failed(new UnsupportedUrlScheme(url))
//    case head :: Nil => head.fetch(url)
//    case head :: tail =>
//      head.fetch(url).recoverWith {
//        case _ => loaderConcat(url, tail)
//      }
//  }
//  def resolve(url: String): Future[Content] = {
//    println("Here1")
//    println("Original loaders: " + defaultEnvironment.loaders)
//
//    val filteredLoaders = defaultEnvironment.loaders.filter(loader=>{
//      println("Loader: " + loader)
//      loader.accepts(url)
//    })
//
//    println("Filtered loaders: " + filteredLoaders)
//
//    val result = loaderConcat(url, filteredLoaders)
//
//    println("Here2")
//    result
//  }


  /** encodes a complete uri. Not encodes chars like / */
  override def encodeURI(url: String): String = URIUtils.encodeURI(url)

  /** encodes a uri component, including chars like / and : */
  override def encodeURIComponent(url: String): String = URIUtils.encodeURIComponent(url)

  /** decode a complete uri. */
  override def decodeURI(url: String): String = URIUtils.decodeURI(url)

  /** decodes a uri component */
  override def decodeURIComponent(url: String): String = URIUtils.decodeURIComponent(url)

  override def normalizeURL(url: String): String = Path.resolve(url)

  override def normalizePath(url: String): String = new URI(encodeURI(url)).normalize.toString

  override def findCharInCharSequence(stream: CharSequence)(p: Char => Boolean): Option[Char] = stream.toString.find(p)

  override def tmpdir(): String = ???

  private def withTrailingSlash(path: String) = {
    (if (!path.startsWith("/")) "/" else "") + path
  }

  protected def createFileLoader(fsProvider: IFSProvider): ResourceLoader = {
    new FileLoader(fsProvider, this)
  }

  protected def createHttpLoader(): ResourceLoader = {
    new HTTPLoader()
  }
}
