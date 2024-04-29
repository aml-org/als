package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08

import amf.apicontract.internal.metamodel.domain.ParameterModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.WebApiPatchedTopLevelKeyTerm
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.RamlBaseUriParameterKeyTerm

object Raml08BaseUriParametersTopLevelKeyTerm
    extends Raml08PatchedKeyTerm
    with RamlBaseUriParameterKeyTerm
    with WebApiPatchedTopLevelKeyTerm

object Raml08BaseUriParametersServerKeyTerm extends Raml08PatchedKeyTerm with RamlBaseUriParameterKeyTerm {
  override val amfObjectName: String = ParameterModel.`type`.head.name
}
