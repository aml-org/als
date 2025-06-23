package org.mulesoft.amfintegration

import amf.core.internal.remote.Spec
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

case class DefinitionWithVendor(documentDefinition: DocumentDefinition, spec: Spec, version: Option[String] = None)

object DefinitionWithVendor {
  def apply(documentDefinition: DocumentDefinition, spec: Spec, version: String) = new DefinitionWithVendor(documentDefinition, spec, Some(version))
}
