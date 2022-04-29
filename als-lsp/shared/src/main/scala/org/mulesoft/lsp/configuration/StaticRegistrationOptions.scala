package org.mulesoft.lsp.configuration

/** Static registration options to be returned in the initialize request.
  *
  * @param id
  *   The id used to register the request. The id can be used to deregister the request again. See also Registration#id.
  */
case class StaticRegistrationOptions(id: Option[String])
