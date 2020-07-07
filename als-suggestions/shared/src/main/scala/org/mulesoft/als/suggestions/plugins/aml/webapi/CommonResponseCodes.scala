package org.mulesoft.als.suggestions.plugins.aml.webapi

trait CommonResponseCodes extends OftenKeysConfig {

  protected def v100: Seq[String] = Seq("100", "101", "102")
  protected def v200: Seq[String] = Seq("200", "201", "202", "203", "204", "205", "206", "207", "208", "226")
  protected def v300: Seq[String] = Seq("300", "301", "302", "303", "304", "305", "306", "307", "308")
  protected def v400: Seq[String] = Seq(
    "400",
    "401",
    "402",
    "403",
    "404",
    "405",
    "406",
    "407",
    "408",
    "409",
    "410",
    "411",
    "412",
    "413",
    "414",
    "415",
    "416",
    "417",
    "418",
    "420",
    "422",
    "423",
    "424",
    "425",
    "426",
    "428",
    "429",
    "431",
    "444",
    "449",
    "450",
    "451",
    "499"
  )
  protected def v500: Seq[String] =
    Seq("500", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "511", "598", "599")

  override lazy val all: Seq[String] = v100 ++ v200 ++ v300 ++ v400 ++ v500
}

object RamlResponseCodes extends CommonResponseCodes

object OasResponseCodes extends OasLikeResponseCodes

trait OasLikeResponseCodes extends CommonResponseCodes {
  override lazy val all: Seq[String] = v100 ++ v200 ++ v300 ++ v400 ++ v500 :+ "default"
}

object Oas30ResponseCodes extends OasLikeResponseCodes {
  override def v100: Seq[String] = Seq("1XX") ++ super.v100
  override def v200: Seq[String] = Seq("2XX") ++ super.v200
  override def v300: Seq[String] = Seq("3XX") ++ super.v300
  override def v400: Seq[String] = Seq("4XX") ++ super.v400
  override def v500: Seq[String] = Seq("5XX") ++ super.v500
}

trait OftenKeysConfig {
  val all: Seq[String]
  lazy val stringValue: String = s"[ ${all.map(quotedMark + _ + quotedMark).mkString(",")}]"
  val quotedMark: String       = ""
}
