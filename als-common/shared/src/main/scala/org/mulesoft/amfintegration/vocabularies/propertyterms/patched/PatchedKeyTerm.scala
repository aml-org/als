package org.mulesoft.amfintegration.vocabularies.propertyterms.patched

import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

trait PatchedKeyTerm extends PropertyTermObjectNode {
  val key: String
  val amfObjectName: String
  val dialectId: String

  private val dialectNames: Map[String, String] =
    Map(
      Raml10TypesDialect.dialect.id -> "raml10",
      Raml08TypesDialect.dialect.id -> "raml08",
      OAS20Dialect.dialect.id       -> "oas2",
      OAS30Dialect.dialect.id       -> "oas3",
      AsyncApi20Dialect.dialect.id  -> "asyncapi2",
      AsyncApi26Dialect.dialect.id  -> "asyncapi2"
    )

  private val dialectName: String      = dialectNames.getOrElse(dialectId, "unknown")
  final override lazy val name: String = s"${dialectName}/$amfObjectName/$key"
}
