package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08

import amf.apicontract.internal.metamodel.domain.security.ParametrizedSecuritySchemeModel

object Raml08SecuredByKeyTerm extends Raml08PatchedKeyTerm {
  override val key: String           = "securedBy"
  override val amfObjectName: String = ParametrizedSecuritySchemeModel.`type`.head.name
  override val description: String =
    "Specifies that all methods described in the API are protected using this security scheme. Applying a security scheme to any method overrides whichever securityScheme is defined here"
}
