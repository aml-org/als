package org.mulesoft.high.level.dialect.dialects

import org.mulesoft.high.level.dialect.DialectConf

object AsyncAPIDialect extends DialectConf {
  override val files: Map[String, String] =
    Map(
      "file:///asyncapi/dialect.yaml"    -> "als-hl/shared/src/main/resources/dialects/asyncapi/dialect.yaml",
      "file:///asyncapi/vocabulary.yaml" -> "als-hl/shared/src/main/resources/dialects/asyncapi/vocabulary.yaml"
    )
  override val rootUrl: String = "file:///asyncapi/dialect.yaml"
}
