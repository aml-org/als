package org.mulesoft.high.level.dialect

import amf.ProfileName

trait DialectConf {
  val files: Map[String, String]
  val rootUrl: String
  val profileName: ProfileName
}
