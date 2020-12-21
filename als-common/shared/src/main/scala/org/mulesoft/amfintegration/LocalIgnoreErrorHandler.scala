package org.mulesoft.amfintegration

import amf.core.annotations.LexicalInformation
import amf.core.errorhandling.ErrorHandler

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
