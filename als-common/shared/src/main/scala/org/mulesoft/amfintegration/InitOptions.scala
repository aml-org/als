package org.mulesoft.amfintegration

import amf.core.remote._

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
class InitOptions(val vendors: Set[Vendor]) {
  def contains(vendor: Vendor): Boolean = vendors.contains(vendor)
}
// todo: vendors instead of profiles?

@JSExportAll
object InitOptions {
  val AllProfiles: InitOptions = new InitOptions(Set(Raml10, Raml08, Oas20, Oas30, AsyncApi20, Aml))

  val WebApiProfiles: InitOptions = new InitOptions(Set(Raml10, Raml08, Oas20, Oas30, AsyncApi20))

  val RamlProfiles: InitOptions = new InitOptions(Set(Raml10, Raml08))

  val OasProfile: InitOptions = new InitOptions(Set(Oas20, Oas30))
}
