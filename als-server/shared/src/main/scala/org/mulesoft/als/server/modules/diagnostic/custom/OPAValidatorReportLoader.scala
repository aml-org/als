package org.mulesoft.als.server.modules.diagnostic.custom

import amf.core.client.common.position
import amf.core.client.common.position.Position
import amf.core.client.common.validation.AmfProfile
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.parser.YMapOps
import org.yaml.model.{YMap, YMapEntry, YNode, YNodeLike}
import org.yaml.parser.JsonParser
import org.yaml.model.YNodeLike.toBoolean

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class OPAValidatorReportLoader {

  implicit class URI(value: String) {
    val SHACL_ALIAS   = "http://www.w3.org/ns/shacl#"
    val TRACE_ALIAS   = "http://a.ml/vocabularies/validation#"
    val AML_ALIAS     = "http://a.ml/vocabularies/amf/parser#"
    def shacl: String = SHACL_ALIAS + value
    def trace: String = TRACE_ALIAS + value
    def aml: String   = AML_ALIAS + value
  }

  def load(report: String): Future[AMFValidationReport] = Future {
    val doc       = JsonParser.apply(report).document()
    val map: YMap = doc.node.as[YMap]
    val conforms  = map.key("conforms".shacl).forall(n => toBoolean(n.value))
    val results   = map.key("result".shacl).map(n => n.value.as[Seq[YMap]]).getOrElse(Nil).map(loadResult)
    AMFValidationReport("", AmfProfile, results)
  }

  def loadResult(map: YMap): AMFValidationResult = {
    val node    = map.key("focusNode".shacl).flatMap(readIdValue).getOrElse("")
    val message = map.key("resultMessage".shacl).flatMap(_.value.asScalar).map(_.text).getOrElse("")
    val level   = map.key("resultSeverity".shacl).flatMap(readIdValue).getOrElse("Violation")
    val lexical = map.key("trace".trace).flatMap(readTrace)
    AMFValidationResult(message, level, node, None, "", lexical, None, null)

  }

  def readTrace(e: YMapEntry): Option[LexicalInformation] = {
    val head = e.value.as[Seq[YNode]].map(_.as[YMap]).head
    head.key("lexicalPosition".aml).map(e => parseLexical(e.value.as[YMap])).map(LexicalInformation(_))
  }

  def parseLexical(m: YMap): position.Range = {
    position.Range(
      m.key("end".aml).map(e => parsePosition(e.value.as[YMap])).getOrElse(Position.ZERO),
      m.key("start".aml).map(e => parsePosition(e.value.as[YMap])).getOrElse(Position.ZERO)
    )
  }

  def parsePosition(m: YMap): Position = {
    new Position(parseNumber(m, "line").getOrElse(1), parseNumber(m, "column").getOrElse(0))
  }

  def parseNumber(m: YMap, field: String): Option[Int] = m.key(field.aml).map(e => e.value.as[Int])

  def readIdValue(e: YMapEntry): Option[String] = {
    e.value.as[YMap].key("@id").flatMap(_.value.asScalar).map(_.text)
  }

}
