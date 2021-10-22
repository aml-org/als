package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml10

import amf.apicontract.internal.metamodel.domain.ServerModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.RamlBaseUriParameterKeyTerm

object Raml10BaseUriKeyTerm extends RamlBaseUriParameterKeyTerm with Raml10PatchedKeyTerm {
  override val amfObjectName: String = ServerModel.`type`.head.name
  override val key: String           = "baseUri"
  override val description: String   = "Information about the network accessible locations where the API is available"
}
