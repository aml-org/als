package org.mulesoft.als.server.feature.serialization

import amf.core.client.common.validation.ProfileNames

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class ConversionConfig(from: String, to: String)

@JSExportAll
object ConversionConfig {

  val RAML10ConvesionToOAS20: ConversionConfig = RAML10ConvesionToOAS20Config

  val RAML10ConvesionToOAS30: ConversionConfig = RAML10ConvesionToOAS30Config

  val OAS20ConvesionToRAML10: ConversionConfig = OAS20ConvesionToRAML10Config

  val OAS20ConvesionToOAS30: ConversionConfig = OAS20ConvesionToOAS30Config

  val OAS30ConvesionToRAML10: ConversionConfig = OAS30ConvesionToRAML10Config

}

// RAML 0.8 conversions are not tested in AMF so we should not support officially those conversion.

object RAML10ConvesionToOAS20Config extends ConversionConfig(ProfileNames.RAML10.profile, ProfileNames.OAS20.profile)

object RAML10ConvesionToOAS30Config extends ConversionConfig(ProfileNames.RAML10.profile, ProfileNames.OAS30.profile)

object OAS20ConvesionToRAML10Config extends ConversionConfig(ProfileNames.OAS20.profile, ProfileNames.RAML10.profile)

object OAS20ConvesionToOAS30Config extends ConversionConfig(ProfileNames.OAS20.profile, ProfileNames.OAS30.profile)

object OAS30ConvesionToRAML10Config extends ConversionConfig(ProfileNames.OAS30.profile, ProfileNames.RAML10.profile)

object AsyncApi2SyntaxConversionConfig
    extends ConversionConfig(ProfileNames.ASYNC20.profile, ProfileNames.ASYNC20.profile)

object OAS20SyntaxConversionConfig extends ConversionConfig(ProfileNames.OAS20.profile, ProfileNames.OAS20.profile)

object OAS30SyntaxConversionConfig extends ConversionConfig(ProfileNames.OAS30.profile, ProfileNames.OAS30.profile)
