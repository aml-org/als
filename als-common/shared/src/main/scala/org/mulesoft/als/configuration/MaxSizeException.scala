package org.mulesoft.als.configuration

import amf.core.client.scala.exception.AmfUnhandledException

case class MaxSizeException(url: String, maxSize: Int)
    extends AmfUnhandledException(s"maximum file size of $maxSize exceeded")
