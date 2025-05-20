package org.mulesoft.als.suggestions.antlr

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.suggestions.interfaces.CompletionProvider
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

object AntlrCompletionProvider {
  def apply(baseUnit: BaseUnit, position: DtoPosition, alsConfigurationState: ALSConfigurationState): CompletionProvider = PlatformAntlrCompletionProvider(baseUnit, position, alsConfigurationState) // defined only in jvm
}