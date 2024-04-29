package org.mulesoft.als.server.textsync

/** Info regarding single text editor.
  */
case class TextDocument(uri: String, version: Int, text: String, syntax: String)
