package org.mulesoft.als.server

import org.mulesoft.als.server.workspace.IndexGlobalDialectTest

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSON

class JsIndexGlobalDialectTest extends IndexGlobalDialectTest {

  override def stringifyJson(str: String): String =
    JSON.stringify(str)
}
