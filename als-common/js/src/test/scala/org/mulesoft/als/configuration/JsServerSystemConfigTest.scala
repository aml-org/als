package org.mulesoft.als.configuration

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.unsafe.PlatformSecrets
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichGenTraversableOnce, _}
import scala.scalajs.js.{Promise, native}

class JsServerSystemConfigTest extends FlatSpec with Matchers with PlatformSecrets {
  behavior of "Js Server configuration test"

  val fakeRL: ClientResourceLoader = js.Dynamic
    .literal(
      accepts = new js.Function1[String, Boolean] {
        override def apply(arg1: String): Boolean = false
      },
      fetch = new js.Function1[String, js.Promise[_]] {
        override def apply(arg1: String): Promise[_] = js.Promise.resolve[String]("")
      }
    )
    .asInstanceOf[ClientResourceLoader]

  val platfromLoaders: List[ResourceLoader] = platform.loaders().toList

  it should "have platform loaders as default" in {
    val jsServerSystemConf: JsServerSystemConf = JsServerSystemConf()
    val loaders                                = jsServerSystemConf.amfConfiguration.resourceLoaders
    loaders.size should be(3)
    loaders should contain allElementsOf platfromLoaders
  }

  it should "not have default platform loaders when given list isn't empty" in {
    val jsServerSystemConf: JsServerSystemConf = JsServerSystemConf(js.Array(fakeRL))
    val loaders                                = jsServerSystemConf.amfConfiguration.resourceLoaders
    loaders.size should be(1)
    loaders should contain noElementsOf platfromLoaders
  }

  it should "keep platform loaders when added" in {
    val jsServerSystemConf: JsServerSystemConf =
      JsServerSystemConf(clientLoaders = (platform.loaders().map(_.toClient) :+ fakeRL).toJSArray)
    val loaders = jsServerSystemConf.amfConfiguration.resourceLoaders
    loaders.size should be(3)
  }

  implicit class nativeRLWrapper(rl: ResourceLoader) {
    def toClient: ClientResourceLoader =
      js.Dynamic
        .literal(
          accepts = new js.Function1[String, Boolean] {
            override def apply(arg1: String): Boolean = rl.accepts(arg1)
          },
          fetch = new js.Function1[String, js.Promise[_]] {
            override def apply(arg1: String): Promise[_] = rl.fetch(arg1).toJSPromise
          }
        )
        .asInstanceOf[ClientResourceLoader]
  }

}
