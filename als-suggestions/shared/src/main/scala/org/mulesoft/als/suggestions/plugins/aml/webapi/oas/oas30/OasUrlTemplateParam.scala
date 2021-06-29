package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.apicontract.client.scala.model.domain.Server
import amf.core.internal.utils.TemplateUri
import org.mulesoft.als.suggestions.plugins.aml.webapi.UrlTemplateParam

object OasUrlTemplateParam extends UrlTemplateParam {
  override protected def serverParams(server: Server): Seq[String] =
    TemplateUri.variables(server.url.option().getOrElse(""))
}
