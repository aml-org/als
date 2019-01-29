package org.mulesoft.language.server.modules.dialectManager

import scala.collection.Map

trait IBundledProject {

  def rootUrl: String

  def files: Map[String, String]
}
