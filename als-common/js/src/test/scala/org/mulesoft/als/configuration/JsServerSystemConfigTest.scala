package org.mulesoft.als.configuration

import amf.core.client.platform.resource.ClientResourceLoader
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.convert.CoreClientConverters.ClientList
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichGenTraversableOnce, _}
import scala.scalajs.js.{Promise, native}

class JsServerSystemConfigTest extends FlatSpec with Matchers with PlatformSecrets {}
