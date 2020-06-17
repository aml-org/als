package org.mulesoft.als.configuration

import org.mulesoft.als.configuration.AlsFormatMime.AlsFormatMime

case class AlsConfiguration(private var formattingOptions: Map[AlsFormatMime, AlsFormattingOptions] = Map())
    extends AlsConfigurationReader {

  private var enableUpdateFormatOptions = true;

  def getFormattingOptions(alsFormatMime: AlsFormatMime): AlsFormattingOptions =
    formattingOptions
      .get(alsFormatMime)
      .orElse(formattingOptions.get(AlsFormatMime.DEFAULT))
      .getOrElse(DefaultAlsFormattingOptions)

  def updateFormattingOptions(options: Map[AlsFormatMime, AlsFormattingOptions]): Unit =
    if (enableUpdateFormatOptions)
      options.foreach(pair => {
        this.formattingOptions + pair
      })

  def setUpdateFormatOptions(enableUpdateFormatOptions: Boolean): Unit = {
    this.enableUpdateFormatOptions = enableUpdateFormatOptions;
  }

  def updateFormatOptionsIsEnabled(): Boolean = enableUpdateFormatOptions;

}
