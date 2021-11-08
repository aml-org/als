package org.mulesoft.als.server.workspace

import amf.aml.client.scala.model.document.DialectInstance
import amf.apicontract.client.scala.AMFConfiguration

case class ValidationProfile(path: String, content: String, model: DialectInstance)

trait ProjectConfigurationProvider {

  def getAMFConfiguration(folder: String): AMFConfiguration
  def getProfiles(folder: String): List[ValidationProfile]
  // TODO: analyze
  // def getValidator:
}
