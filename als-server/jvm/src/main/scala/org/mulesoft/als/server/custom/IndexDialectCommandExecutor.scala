package org.mulesoft.als.server.custom

import com.google.gson.JsonElement
import org.mulesoft.lsp.textsync.IndexDialectParams

object IndexDialectCommandExecutor extends CommandExecutor[IndexDialectParams] {

  override def matcher: AnyRef => Option[IndexDialectParams] = {
    case dpf: IndexDialectParams => Some(dpf)
    case json: JsonElement       => parseJson(json)
    case _                       => None
  }
}
