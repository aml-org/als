package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.utils.TemplateUri
import amf.plugins.domain.webapi.models.Server
import org.mulesoft.als.suggestions.plugins.aml.webapi.UrlTemplateParam

object OasUrlTemplateParam extends UrlTemplateParam {
  override protected def serverParams(server: Server): Seq[String] =
    TemplateUri.variables(server.url.option().getOrElse(""))
}
