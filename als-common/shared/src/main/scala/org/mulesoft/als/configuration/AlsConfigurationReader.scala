package org.mulesoft.als.configuration

trait AlsConfigurationReader {

  // todo: add optional "experimental" feature? (to enable beta changes)
  def getFormatOptionForMime(mimeType: String): AlsFormatOptions
  def supportsDocumentChanges: Boolean
}
