package org.mulesoft.als.server.modules.diagnostic.custom

import amf.core.client.common.position
import amf.core.client.common.position.Position
import amf.core.client.common.validation.AmfProfile
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.parser.YMapOps
import org.yaml.model.YNodeLike.toBoolean
import org.yaml.model.{YMap, YMapEntry, YNode}
import org.yaml.parser.JsonParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class OPAValidatorReportLoader {

  implicit class URI(value: String) {
    val SHACL_ALIAS        = "http://www.w3.org/ns/shacl#"
    def stripShacl: String = value.replace(SHACL_ALIAS, "")
  }

  def load(report: String): Future[AMFValidationReport] = Future {
    val doc       = JsonParser.apply(report).document()
    val map: YMap = doc.node.as[YMap]
    val conforms  = map.key("conforms").forall(n => toBoolean(n.value))
    val results   = map.key("result").map(n => n.value.as[Seq[YMap]]).getOrElse(Nil).map(loadResult)
    AMFValidationReport("", AmfProfile, results)
  }

  def loadResult(map: YMap): AMFValidationResult = {
    val node                = map.key("focusNode").flatMap(readIdValue).getOrElse("")
    val message             = map.key("resultMessage").flatMap(_.value.asScalar).map(_.text).getOrElse("")
    val level               = map.key("resultSeverity").flatMap(s => readIdValue(s).map(_.stripShacl)).getOrElse("Violation")
    val (lexical, location) = map.key("trace").map(readTrace).getOrElse((None, None))
    AMFValidationResult(message, level, node, None, "", lexical, location, null)

  }

  def readTraceValue(e: YMapEntry): Seq[AMFValidationResult] =
    e.value.as[YMap].key("subResult").map(_.value.as[Seq[YMap]]).getOrElse(Nil).map(loadResult)

  def readTrace(e: YMapEntry): (Option[LexicalInformation], Option[String]) = {
    val head      = e.value.as[Seq[YNode]].map(_.as[YMap]).head
    val subResult = head.key("traceValue").map(readTraceValue).getOrElse(Seq.empty)
    val location  = head.key("location").map(e => parseLocation(e.value.as[YMap]))
    location.getOrElse((subResult.headOption.flatMap(_.position), subResult.headOption.flatMap(_.location)))
  }

  def parseLocation(m: YMap): (Option[LexicalInformation], Option[String]) = {
    val position = m.key("range").map(e => parseRange(e.value.as[YMap])).map(LexicalInformation(_))
    val location = m.key("uri").map(_.value.toString())
    (position, if (location.contains("\"\"")) None else location)
  }

  def parseRange(m: YMap): position.Range = {
    position.Range(
      m.key("start").map(e => parsePosition(e.value.as[YMap])).getOrElse(Position.ZERO),
      m.key("end").map(e => parsePosition(e.value.as[YMap])).getOrElse(Position.ZERO)
    )
  }

  def parsePosition(m: YMap): Position = {
    new Position(parseNumber(m, "line").getOrElse(1), parseNumber(m, "column").getOrElse(0))
  }

  def parseNumber(m: YMap, field: String): Option[Int] = m.key(field).map(e => e.value.as[Int])

  def readIdValue(e: YMapEntry): Option[String] = {
    e.value.as[YMap].key("@id").flatMap(_.value.asScalar).map(_.text)
  }

}
