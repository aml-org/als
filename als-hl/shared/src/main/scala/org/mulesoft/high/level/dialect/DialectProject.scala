package org.mulesoft.high.level.dialect

import scala.collection.Map

trait DialectProject {

  val files: Map[String, String]
  val rootUrl: String
}
