package org.mulesoft.als.server.modules.completion

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

case class CompletionReferenceResolver(extensions: Seq[Dialect],
                                       profiles: Seq[ValidationProfile],
                                       config: ProjectConfiguration,
                                       results: Seq[AMFParseResult],
                                       resourceLoaders: Seq[ResourceLoader],
                                       projectErrors: Seq[AMFValidationResult],
                                       bu: BaseUnit)
    extends ProjectConfigurationState {
  override def cache: Seq[BaseUnit] = bu.flatRefs
}

object CompletionReferenceResolver {
  def apply(p: ProjectConfigurationState, bu: BaseUnit): CompletionReferenceResolver = {
    new CompletionReferenceResolver(
      p.extensions,
      p.profiles,
      p.config,
      p.results,
      p.resourceLoaders,
      p.projectErrors,
      bu
    )
  }
}
