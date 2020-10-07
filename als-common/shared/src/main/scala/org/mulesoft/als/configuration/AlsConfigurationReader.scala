package org.mulesoft.als.configuration

trait AlsConfigurationReader {

  def getFormatOptionForMime(mimeType: String): AlsFormatOptions
  def supportsDocumentChanges: Boolean
}
