package org.mulesoft.als.configuration

trait AlsConfigurationReader {

  def getFormattingOptions(mimeType: String): AlsFormatOptions

}
