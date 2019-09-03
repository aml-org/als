package org.mulesoft.amfmanager

import amf.ProfileName

import scala.language.postfixOps

case class CustomDialects(name: ProfileName,
                          url: String,
                          content: String,
                          customVocabulary: Option[CustomVocabulary] = None)

case class CustomVocabulary(url: String, content: String)
