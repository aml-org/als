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
    val TRACE_ALIAS        = "http://a.ml/vocabularies/validation#"
    val LEXICAL_ALIAS      = "http://a.ml/vocabularies/lexical#"
    val AML_ALIAS          = "http://a.ml/vocabularies/amf/parser#"
    def shacl: String      = SHACL_ALIAS + value
    def stripShacl: String = value.replace(SHACL_ALIAS, "")
    def trace: String      = TRACE_ALIAS + value
    def aml: String        = AML_ALIAS + value
    def lexical: String    = LEXICAL_ALIAS + value
  }

  def load(report: String): Future[AMFValidationReport] = Future {
    val doc       = JsonParser.apply(report).document()
    val map: YMap = doc.node.as[YMap]
    val conforms  = map.key("conforms".shacl).forall(n => toBoolean(n.value))
    val results   = map.key("result".shacl).map(n => n.value.as[Seq[YMap]]).getOrElse(Nil).map(loadResult)
    AMFValidationReport("", AmfProfile, results)
  }

  def loadResult(map: YMap): AMFValidationResult = {
    val node                = map.key("focusNode".shacl).flatMap(readIdValue).getOrElse("")
    val message             = map.key("resultMessage".shacl).flatMap(_.value.asScalar).map(_.text).getOrElse("")
    val level               = map.key("resultSeverity".shacl).flatMap(s => readIdValue(s).map(_.stripShacl)).getOrElse("Violation")
    val (lexical, location) = map.key("trace".trace).map(readTrace).getOrElse((None, None))
    AMFValidationResult(message, level, node, None, "", lexical, location, null)

  }

  def readTrace(e: YMapEntry): (Option[LexicalInformation], Option[String]) = {
    val head = e.value.as[Seq[YNode]].map(_.as[YMap]).head
    head.key("location".trace).map(e => parseLocation(e.value.as[YMap])).getOrElse((None, None))
  }

  def parseLocation(m: YMap): (Option[LexicalInformation], Option[String]) = {
    val position = m.key("range".lexical).map(e => parseRange(e.value.as[YMap])).map(LexicalInformation(_))
    val location = m.key("uri".lexical).map(_.value.toString())
    (position, if (location.contains("\"\"")) None else location)
  }

  def parseRange(m: YMap): position.Range = {
    position.Range(
      m.key("start".lexical).map(e => parsePosition(e.value.as[YMap])).getOrElse(Position.ZERO),
      m.key("end".lexical).map(e => parsePosition(e.value.as[YMap])).getOrElse(Position.ZERO)
    )
  }

  def parsePosition(m: YMap): Position = {
    new Position(parseNumber(m, "line").getOrElse(1), parseNumber(m, "column").getOrElse(0))
  }

  def parseNumber(m: YMap, field: String): Option[Int] = m.key(field.lexical).map(e => e.value.as[Int])

  def readIdValue(e: YMapEntry): Option[String] = {
    e.value.as[YMap].key("@id").flatMap(_.value.asScalar).map(_.text)
  }

}
