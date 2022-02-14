package org.mulesoft.als.configuration

import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider

// todo: obsolete?
object Configuration {
  val internalDialects
    : Set[String] = Set("http://a.ml/dialects/profile.raml") ++ BaseAlsDialectProvider.allBaseDialects
    .map(_.id)
}
