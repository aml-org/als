package org.mulesoft.amfintegration.amfconfiguration.executioncontext

import amf.core.internal.unsafe.PlatformBuilder

import scala.concurrent.ExecutionContext

object Implicits {
  implicit lazy val global: ExecutionContext = PlatformBuilder().globalExecutionContext
}
