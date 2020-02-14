package org.mulesoft.als.server.feature.serialization

case class ConversionClientCapabilities(supported: Boolean)

case class ConversionRequestOptions(supported: Seq[ConversionConfig])
