package org.mulesoft.amfintegration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.{Dialect, DialectInstance}
import amf.core.client.common.transform.PipelineId
import amf.core.client.scala.model.document.BaseUnit

case class ValidationProfile(path: String, content: String, model: DialectInstance, definedBy: Dialect) {
  def toEntry: (String, DialectInstance) = path -> model

  lazy val resolved: BaseUnit =
    AMLConfiguration.predefined().withDialect(definedBy).baseUnitClient().transform(model, PipelineId.Default).baseUnit
}
