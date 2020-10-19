package org.mulesoft.als.configuration

case class AlsConfiguration(private var formattingOptions: Map[String, AlsFormattingOptions] = Map())
    extends AlsConfigurationReader {

  private var enableUpdateFormatOptions = true
  private var sDocumentChanges: Boolean = false

  override def supportsDocumentChanges: Boolean        = this.sDocumentChanges
  def supportsDocumentChanges(supports: Boolean): Unit = this.sDocumentChanges = supports

  def getFormatOptionForMime(mimeType: String): AlsFormatOptions =
    formattingOptions.getOrElse(mimeType, DefaultAlsFormattingOptions)

  def updateFormattingOptions(options: Map[String, AlsFormattingOptions]): Unit =
    if (enableUpdateFormatOptions)
      options.foreach(pair => {
        this.formattingOptions += pair
      })

  def setUpdateFormatOptions(enableUpdateFormatOptions: Boolean): Unit =
    this.enableUpdateFormatOptions = enableUpdateFormatOptions

  def getFormatOptions: Map[String, AlsFormattingOptions] = formattingOptions

  def updateFormatOptionsIsEnabled(): Boolean = enableUpdateFormatOptions

}
