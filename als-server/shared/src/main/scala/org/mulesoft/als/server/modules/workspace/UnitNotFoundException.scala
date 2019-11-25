package org.mulesoft.als.server.modules.workspace

case class UnitNotFoundException(uri: String) extends Exception(s"Unit not found at repository for uri: $uri")
