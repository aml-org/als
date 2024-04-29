package org.mulesoft.als.server.textsync

/** Document being opened.
  */
case class OpenedDocument(
    /** Document URI
      */
    var uri: String,

    /** Optional document version.
      */
    var version: Int,

    /** Optional document content
      */
    var text: String
)
