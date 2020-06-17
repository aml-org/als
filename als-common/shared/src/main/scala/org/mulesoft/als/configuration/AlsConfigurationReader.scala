package org.mulesoft.als.configuration

import org.mulesoft.als.configuration.AlsFormatMime.AlsFormatMime

trait AlsConfigurationReader {

  def getFormattingOptions(alsFormatMime: AlsFormatMime): AlsFormattingOptions

}
