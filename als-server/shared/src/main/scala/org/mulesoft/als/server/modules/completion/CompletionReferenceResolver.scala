package org.mulesoft.als.server.modules.completion

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

case class CompletionReferenceResolver(p: ProjectConfigurationState, bu: BaseUnit)
    extends ProjectConfigurationState() {
  override def cache: Seq[BaseUnit]                                                                  = bu.flatRefs
  override val extensions: Seq[Dialect]                                                              = p.extensions
  override val profiles: scala.Seq[_root_.org.mulesoft.amfintegration.ValidationProfile]             = p.profiles
  override val config: _root_.org.mulesoft.als.configuration.ProjectConfiguration                    = p.config
  override val results: scala.Seq[_root_.amf.core.client.scala.AMFParseResult]                       = p.results
  override val resourceLoaders: scala.Seq[_root_.amf.core.client.scala.resource.ResourceLoader]      = p.resourceLoaders
  override val projectErrors: scala.Seq[_root_.amf.core.client.scala.validation.AMFValidationResult] = p.projectErrors
}
