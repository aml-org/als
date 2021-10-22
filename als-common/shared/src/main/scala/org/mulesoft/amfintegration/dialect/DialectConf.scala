package org.mulesoft.amfmanager.dialect

import amf.core.client.common.validation.ProfileName

trait DialectConf {
  val files: Map[String, String]
  val rootUrl: String
  val profileName: ProfileName
}
