package org.mulesoft.amfintegration

import amf.aml.client.scala.model.document.DialectInstance

case class ValidationProfile(path: String, content: String, model: DialectInstance) {
  def toEntry: (String, DialectInstance) = path -> model
}