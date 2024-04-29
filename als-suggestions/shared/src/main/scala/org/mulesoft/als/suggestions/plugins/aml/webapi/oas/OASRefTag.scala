package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.security.Settings
import amf.apicontract.client.scala.model.domain.{EndPoint, Request}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.AMLRefTagCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.IsInsideRequired

object OASRefTag extends AMLRefTagCompletionPlugin with IsInsideRequired {

  override protected def isObjectDeclarable(params: AmlCompletionRequest): Boolean =
    super.isObjectDeclarable(params) || ((params.amfObject
      .isInstanceOf[EndPoint] || params.amfObject.isInstanceOf[Request]) && !params.astPartBranch.isKeyDescendantOf(
      "parameters"
    )) || params.amfObject.isInstanceOf[Settings]

  override def isExceptionCase(branch: YPartBranch): Boolean = isInsideRequired(branch)
}
