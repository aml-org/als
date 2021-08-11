package org.mulesoft.als.configuration

import org.mulesoft.lsp.configuration.{DefaultFormattingOptions, FormatOptions, FormattingOptions}

case class AlsConfiguration(private var formattingOptions: Map[String, FormattingOptions] = Map(),
                            private var templateType: TemplateTypes.TemplateTypes = TemplateTypes.FULL,
                            private var prettyPrintSerialization: Boolean = false)
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

  override def getTemplateType: TemplateTypes.TemplateTypes = this.templateType

  def setTemplateType(templateType: TemplateTypes.TemplateTypes): Unit = this.templateType = templateType

  def getShouldPrettyPrintSerialization: Boolean = prettyPrintSerialization

  def setShouldPrettyPrintSerialization(value: Boolean): Unit = this.prettyPrintSerialization = value
}

object TemplateTypes extends Enumeration {
  type TemplateTypes = String
  val NONE   = "NONE"
  val SIMPLE = "SIMPLE"
  val FULL   = "FULL"
}
