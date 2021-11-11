package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.AMFConfiguration
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.ValidationProfile

abstract class ProjectConfigurationState(val extensions: Seq[Dialect],
                                         val profiles: Seq[ValidationProfile],
                                         val config: ProjectConfiguration,
                                         val results: Seq[AMFParseResult],
                                         val resourceLoaders: Seq[ResourceLoader]) {

  def customSetUp(amfConfiguration: AMFConfiguration): AMFConfiguration = amfConfiguration
  def cache: Seq[BaseUnit]
}

case class EmptyProjectConfigurationState(folder: String)
    extends ProjectConfigurationState(Seq.empty, Seq.empty, ProjectConfiguration.empty(folder), Seq.empty, Seq.empty) {
  override val cache: Seq[BaseUnit] = Seq.empty
}

object EmptyProjectConfigurationState {
  def apply(): EmptyProjectConfigurationState = EmptyProjectConfigurationState("")
}
