package org.mulesoft.language.outline.common.commonInterfaces

/**
  * Document that has its text changed
  */
case class IChangedDocument(
    /**
      * Document URI
      */
    var uri: String,
    /**
      * Optional document version.
      */
    var version: Int,
    /**
      * Optional document content
      */
    var text: Option[String],
    /**
      * Optional set of text edits instead of complete text replacement.
      * Is only taken into account if text is null.
      */
    var textEdits: Option[Seq[ITextEdit]]
)
