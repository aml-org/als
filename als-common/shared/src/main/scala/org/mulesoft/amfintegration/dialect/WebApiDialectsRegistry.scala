package org.mulesoft.amfintegration.dialect

import amf.core.model.document.BaseUnit
import amf.core.remote.{Oas20, Raml08, Raml10}
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstanceUnit}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect
import org.mulesoft.amfintegration.dialect.dialects.raml.{RAML08Dialect, RAML10Dialect}

object WebApiDialectsRegistry {

  def dialectFor(bu: BaseUnit): Option[Dialect] = {
    bu match {
      case di: DialectInstanceUnit => AMLPlugin.registry.dialectFor(di)
      case _ =>
        bu.sourceVendor match {
          case Some(Oas20)  => Some(OAS20Dialect())
          case Some(Raml10) => Some(RAML10Dialect())
          case Some(Raml08) => Some(RAML08Dialect())
          case _            => None
        }
    }
  }
}
