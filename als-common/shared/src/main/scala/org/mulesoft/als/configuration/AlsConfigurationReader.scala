package org.mulesoft.als.configuration

import org.mulesoft.lsp.configuration.FormatOptions

trait AlsConfigurationReader {
  // todo: add optional "experimental" feature? (to enable beta changes)
  def getFormatOptionForMime(mimeType: String): FormatOptions
  def supportsDocumentChanges: Boolean
  def getTemplateType: TemplateTypes.TemplateTypes
}
