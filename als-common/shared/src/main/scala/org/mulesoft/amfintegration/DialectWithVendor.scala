package org.mulesoft.amfintegration

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.remote.Spec

case class DialectWithVendor(dialect: Dialect, spec: Spec, version: Option[String] = None)

object DialectWithVendor {
  def apply(dialect: Dialect, spec: Spec, version: String) = new DialectWithVendor(dialect, spec, Some(version))
}
