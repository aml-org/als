package org.mulesoft.typesystem.definition.system

trait CommonResponseCodes {

  protected val v100: Seq[String] = Seq("100", "101", "102")
  protected val v200: Seq[String] = Seq("200", "201", "202", "203", "204", "205", "206", "207", "208", "226")
  protected val v300: Seq[String] = Seq("300", "301", "302", "303", "304", "305", "306", "307", "308")
  protected val v400: Seq[String] = Seq(
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
  protected val v500: Seq[String] =
    Seq("500", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "511", "598", "599")

  val all: Seq[String]         = v100 ++ v200 ++ v300 ++ v400 ++ v500
  lazy val stringValue: String = "[" + all.mkString(",") + "]"
}

object RamlResponseCodes extends CommonResponseCodes

object OasResponseCodes extends CommonResponseCodes {
  override val all: Seq[String] = v100 ++ v200 ++ v300 ++ v400 ++ v500 ++ Seq("default")
}
