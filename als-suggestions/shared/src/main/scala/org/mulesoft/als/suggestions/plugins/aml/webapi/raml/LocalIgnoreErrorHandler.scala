package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.LexicalInformation
import amf.core.parser.ErrorHandler

object LocalIgnoreErrorHandler extends ErrorHandler {
  override def reportConstraint(id: String,
                                node: String,
                                property: Option[String],
                                message: String,
                                lexical: Option[LexicalInformation],
                                level: String,
                                location: Option[String]): Unit = {
    println(s"Error in local resolution.\nMessage: $message\nNode: $node")
  }

}
