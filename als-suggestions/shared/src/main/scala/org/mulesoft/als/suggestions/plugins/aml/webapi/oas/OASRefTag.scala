package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.AmfObject
import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.plugins.aml.AMLRefTagCompletionPlugin

object OASRefTag extends AMLRefTagCompletionPlugin {

  override protected def isObjectDeclarable(params: AmlCompletionRequest): Boolean =
    super.isObjectDeclarable(params) || (params.amfObject
      .isInstanceOf[EndPoint] && !params.yPartBranch.isKeyDescendanceOf("parameters"))
}
