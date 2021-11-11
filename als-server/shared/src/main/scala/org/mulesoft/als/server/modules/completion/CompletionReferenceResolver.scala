package org.mulesoft.als.server.modules.completion

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

case class CompletionReferenceResolver(p: ProjectConfigurationState, bu: BaseUnit)
    extends ProjectConfigurationState(p.extensions, p.profiles, p.config, p.results, p.resourceLoaders) {
  override def cache: Seq[BaseUnit] = bu.flatRefs
}
