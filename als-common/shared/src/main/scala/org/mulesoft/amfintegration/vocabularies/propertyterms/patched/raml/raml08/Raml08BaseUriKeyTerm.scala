package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08

import amf.plugins.domain.webapi.metamodel.ServerModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.WebApiPatchedTopLevelKeyTerm
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.RamlBaseUriKeyTerm

object Raml08BaseUriTopLevelKeyTerm
    extends Raml08PatchedKeyTerm
    with RamlBaseUriKeyTerm
    with WebApiPatchedTopLevelKeyTerm

object Raml08BaseUriServerKeyTerm extends Raml08PatchedKeyTerm with RamlBaseUriKeyTerm {
  override val amfObjectName: String = ServerModel.`type`.head.name
}
