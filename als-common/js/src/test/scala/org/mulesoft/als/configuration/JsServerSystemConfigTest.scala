package org.mulesoft.als.configuration

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce

class JsServerSystemConfigTest extends FlatSpec with Matchers with PlatformSecrets {
  behavior of "Js Server configuration test"

  val fakeRL: ClientResourceLoader = new ResourceLoader {
    override def fetch(resource: String): Future[Content] = Future.successful(new Content("t", "t"))

    override def accepts(resource: String): Boolean = false
  }.asInstanceOf[ClientResourceLoader]

  val platfromLoaders: List[ResourceLoader] = platform.loaders().toList

  it should "have platform loaders as default" in {
    val jsServerSystemConf: JsServerSystemConf = JsServerSystemConf()
    val loaders                                = jsServerSystemConf.amfConfiguration.resourceLoaders
    loaders.size should be(2)
    loaders should contain allElementsOf platfromLoaders
  }

  it should "not have default platform loaders when given list isn't empty" in {
    val jsServerSystemConf: JsServerSystemConf = JsServerSystemConf(js.Array(fakeRL))
    val loaders                                = jsServerSystemConf.amfConfiguration.resourceLoaders
    loaders.size should be(1)
    loaders should contain noElementsOf platfromLoaders
  }

  it should "keep platform loaders when added" in {
    val jsServerSystemConf: JsServerSystemConf = JsServerSystemConf(
      clientLoaders = (platform.loaders().map(_.asInstanceOf[ClientResourceLoader]) :+ fakeRL).toJSArray)
    val loaders = jsServerSystemConf.amfConfiguration.resourceLoaders
    loaders.size should be(3)
  }

}
