package org.mulesoft.amfintegration.dialect.extensions

object ApiExtensions extends Enumeration {

  val RAML = ".raml"
  val YAML = ".yaml"
  val JSON = ".json"

  private val extensions = Seq(RAML, YAML, JSON)

  def getValidExtensions: Seq[String] = extensions
  def isValidExtension(extension: String): Boolean =
    extensions.contains(extension.toLowerCase())
}
