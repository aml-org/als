package org.mulesoft.als.configuration

case class AlsConfiguration(private var formattingOptions: Option[AlsFormattingOptions] = None)
    extends AlsConfigurationReader {

  private var enableUpdateFormatOptions = true;

  def getFormattingOptions: AlsFormattingOptions = formattingOptions.getOrElse(DefaultAlsFormattingOptions)

  def updateFormattingOptions(formattingOptions: AlsFormattingOptions): Unit = {
    this.formattingOptions = Some(formattingOptions)
  }

  def setUpdateFormatOptions(enableUpdateFormatOptions: Boolean): Unit = {
    this.enableUpdateFormatOptions = enableUpdateFormatOptions;
  }

  def updateFormatOptionsIsEnabled(): Boolean = enableUpdateFormatOptions;

}
