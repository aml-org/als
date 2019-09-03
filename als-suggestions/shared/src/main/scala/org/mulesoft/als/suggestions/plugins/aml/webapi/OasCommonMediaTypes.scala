package org.mulesoft.als.suggestions.plugins.aml.webapi

object OasCommonMediaTypes extends OftenKeysConfig {

  override val all: Seq[String] = Seq(
    "text/plain; charset=utf-8",
    "application/json",
    "application/vnd.github+json",
    "application/vnd.github.v3+json",
    "application/vnd.github.v3.raw+json",
    "application/vnd.github.v3.text+json",
    "application/vnd.github.v3.html+json",
    "application/vnd.github.v3.full+json",
    "appxlication/vnd.github.v3.diff",
    "application/vnd.github.v3.patch"
  )
  override val quotedMark: String = "\""
}

object RamlCommonMediaTypes extends OftenKeysConfig {

  override val all: Seq[String] = Seq(
    "application/json",
    "application/xml"
  )
  override val quotedMark: String = "\""
}