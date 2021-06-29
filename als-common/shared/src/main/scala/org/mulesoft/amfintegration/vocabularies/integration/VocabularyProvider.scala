package org.mulesoft.amfintegration.vocabularies.integration

import amf.core.client.scala.vocabulary.ValueType

trait VocabularyProvider {
  def getDescription(base: String, name: String): Option[String]
  def getDescription(valueType: ValueType): Option[String]
}
