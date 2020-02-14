package org.mulesoft.als.server.custom

import com.google.gson.JsonElement
import org.mulesoft.als.server.protocol.textsync.DidFocusParams

object DidFocusCommandParamDeserializer extends CommandParamDeserializer[DidFocusParams] {
  override def matcher: AnyRef => Option[DidFocusParams] = {
    case dpf: DidFocusParams => Some(dpf)
    case json: JsonElement   => parseJson(json)
    case _                   => None
  }
}
