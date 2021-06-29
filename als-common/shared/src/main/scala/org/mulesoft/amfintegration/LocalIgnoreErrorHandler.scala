package org.mulesoft.amfintegration

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.annotations.LexicalInformation

object LocalIgnoreErrorHandler extends AMFErrorHandler {

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
