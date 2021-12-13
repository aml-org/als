package org.mulesoft.als.server.lsp4j

import com.google.gson.GsonBuilder
import org.mulesoft.als.server.workspace.IndexGlobalDialectTest

class JvmIndexGlobalDialectTest extends IndexGlobalDialectTest {
  override def stringifyJson(str: String): String =
    new GsonBuilder().create().toJson(str)
}
