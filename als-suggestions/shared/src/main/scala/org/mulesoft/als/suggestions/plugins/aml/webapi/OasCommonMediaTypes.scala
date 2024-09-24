package org.mulesoft.als.suggestions.plugins.aml.webapi

import org.mulesoft.amfintegration.dialect.OasLikeContentTypes

object Oas20CommonMediaTypes extends OasLikeContentTypes with OftenKeysConfig {
  override val all: Seq[String] = mediaTypes
}
object Oas30CommonMediaTypes extends OasLikeContentTypes with OftenKeysConfig {

  override val all: Seq[String] = super.mediaTypes :+ "application/xml"
}
object RamlCommonMediaTypes extends OftenKeysConfig {

  override val all: Seq[String] = Seq(
    "application/json",
    "application/xml"
  )
}
