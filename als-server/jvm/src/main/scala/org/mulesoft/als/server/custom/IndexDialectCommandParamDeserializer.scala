package org.mulesoft.als.server.custom

import com.google.gson.JsonElement
import org.mulesoft.als.server.protocol.textsync.IndexDialectParams

object IndexDialectCommandParamDeserializer extends CommandParamDeserializer[IndexDialectParams] {

  override def matcher: AnyRef => Option[IndexDialectParams] = {
    case dpf: IndexDialectParams => Some(dpf)
    case json: JsonElement       => parseJson(json)
    case _                       => None
  }
}
