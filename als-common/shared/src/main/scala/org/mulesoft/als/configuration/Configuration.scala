package org.mulesoft.als.configuration

import org.mulesoft.amfintegration.dialect.integration.BaseAlsDefinitionsProvider

// todo: obsolete?
object Configuration {
  val internalDialects: Set[String] = Set("http://a.ml/dialects/profile.raml") ++ BaseAlsDefinitionsProvider.allBaseDefinitions
    .map(_.baseUnit.id)
}
