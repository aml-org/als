package org.mulesoft.amfmanager.dialect

import amf.ProfileName

trait DialectConf {
  val files: Map[String, String]
  val rootUrl: String
  val profileName: ProfileName
}
