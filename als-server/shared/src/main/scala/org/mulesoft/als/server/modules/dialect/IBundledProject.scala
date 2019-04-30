package org.mulesoft.als.server.modules.dialect

import scala.collection.Map

trait IBundledProject {

  def rootUrl: String

  def files: Map[String, String]
}
