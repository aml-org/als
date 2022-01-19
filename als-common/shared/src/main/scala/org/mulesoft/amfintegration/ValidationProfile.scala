package org.mulesoft.amfintegration

import amf.aml.client.scala.model.document.{DialectInstance, Dialect}

case class ValidationProfile(path: String, content: String, model: DialectInstance, definedBy: Dialect) {
  def toEntry: (String, DialectInstance) = path -> model
}
