package org.mulesoft.amfintegration.vocabularies

import amf.core.parser.Annotations
import amf.plugins.document.vocabularies.model.document.Vocabulary

trait VocabularyObject {

  protected def base: String
  protected def classes: Seq[ClassTermObjectNode]
  protected def properties: Seq[PropertyTermObjectNode]
  def apply(): Vocabulary =
    Vocabulary()
      .withId(base)
      .withBase(base)
      .withDeclares(classes.map(_.obj) ++ properties.map(_.obj))
}
