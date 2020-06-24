package org.mulesoft.amfintegration.vocabularies.propertyterms

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object CommentPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "comment"
  override val description: String = "A comment on an item. The comment's content is expressed via the text"
}
