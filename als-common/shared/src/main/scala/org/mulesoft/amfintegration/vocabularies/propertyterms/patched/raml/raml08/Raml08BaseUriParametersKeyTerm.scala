package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08

import amf.plugins.domain.webapi.metamodel.ServerModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.RamlBaseUriParameterKeyTerm

object Raml08BaseUriParametersTopLevelKeyTerm
    extends Raml08PatchedKeyTerm
    with RamlBaseUriParameterKeyTerm
    with PatchedTopLevelKeyTerm

object Raml08BaseUriParametersServerKeyTerm extends Raml08PatchedKeyTerm with RamlBaseUriParameterKeyTerm {
  override val amfObjectName: String = ServerModel.`type`.head.name
}
