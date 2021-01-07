package org.mulesoft.als.configuration

import org.mulesoft.lsp.configuration.{FormatOptions, FormattingOptions, DefaultFormattingOptions}

case class AlsConfiguration(private var formattingOptions: Map[String, FormattingOptions] = Map(),
                            private var disableTemplates: Boolean = false)
    extends AlsConfigurationReader {

  private var enableUpdateFormatOptions = true
  private var sDocumentChanges: Boolean = false

  override def supportsDocumentChanges: Boolean        = this.sDocumentChanges
  def supportsDocumentChanges(supports: Boolean): Unit = this.sDocumentChanges = supports

  def getFormatOptionForMime(mimeType: String): FormatOptions =
    formattingOptions.getOrElse(mimeType, DefaultFormattingOptions)

  def updateFormattingOptions(options: Map[String, FormattingOptions]): Unit =
    if (enableUpdateFormatOptions)
      options.foreach(pair => {
        this.formattingOptions += pair
      })

  def setUpdateFormatOptions(enableUpdateFormatOptions: Boolean): Unit =
    this.enableUpdateFormatOptions = enableUpdateFormatOptions

  def getFormatOptions: Map[String, FormattingOptions] = formattingOptions

  def updateFormatOptionsIsEnabled(): Boolean = enableUpdateFormatOptions

  override def isDisableTemplates: Boolean              = this.disableTemplates
  def disableTemplates(disableTemplates: Boolean): Unit = this.disableTemplates = disableTemplates
}
