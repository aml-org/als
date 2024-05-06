package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{
  AsyncApiSecuritySchemeObject,
  AsyncApiSecuritySettingsObject
}

trait Async21Types {
  protected def async21Types: Seq[String] = Seq(
    "scramSha256",
    "scramSha512",
    "Gssapi"
  )
}
object AsyncApi26SecuritySchemeObject extends AsyncApiSecuritySchemeObject with Async21Types {
  override protected val baseLocation       = AsyncApi26Dialect.DialectLocation
  override protected def types: Seq[String] = super.types ++ async21Types
}

object AsyncApi26SecuritySettingsObject extends AsyncApiSecuritySettingsObject with Async21Types {
  override protected def types: Seq[String] = super.types ++ async21Types
}
