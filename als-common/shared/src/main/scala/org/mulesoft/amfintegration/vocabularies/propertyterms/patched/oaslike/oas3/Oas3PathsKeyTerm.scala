package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas3

import amf.plugins.domain.webapi.metamodel.EndPointModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.OaslikePathsKeyTerm

object Oas3PathsKeyTerm extends Oas3PatchedKeyTerm with OaslikePathsKeyTerm {
  override val amfObjectName: String = EndPointModel.`type`.head.name
}
