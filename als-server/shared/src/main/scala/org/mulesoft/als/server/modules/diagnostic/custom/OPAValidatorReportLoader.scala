package org.mulesoft.als.server.modules.diagnostic.custom

import amf.core.client.common.position
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.parser.YMapOps
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.diagnostic.AlsValidationResult
import org.mulesoft.amfintegration.ParserRangeImplicits.RangeImplicit
import org.mulesoft.lsp.feature.common.{Location, Range}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation
import org.yaml.model.YNodeLike.toBoolean
import org.yaml.model.{YMap, YMapEntry}
import org.yaml.parser.JsonParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OPAValidatorReportLoader {

  def load(report: String,
           uri: String,
           profileName: String,
           profile: Option[ValidationProfileWrapper]): Future[Seq[AlsValidationResult]] = Future {
    val doc       = JsonParser.apply(report).document()
    val map: YMap = doc.node.as[YMap]
    val conforms  = map.key("conforms").forall(n => toBoolean(n.value))
    val results = map
      .key("result")
      .map(n => n.value.as[Seq[YMap]])
      .getOrElse(Nil)
      .map(new ResultParser(_, uri, profile).build(profileName))
    results
  }

}

trait JsonLDReader {

  val unknownLocation: Location =
    Location("unknown", LspRangeConverter.toLspRange(position.Range.NONE.toPositionRange))

  def parseRange(m: YMap): Option[position.Range] =
    m.key("range").map(e => parseInsideRange(e.value.as[YMap]))

  protected def parseInsideRange(m: YMap): position.Range = {
    position.Range(
      m.key("start").map(e => parsePosition(e.value.as[YMap])).getOrElse(position.Position.ZERO),
      m.key("end").map(e => parsePosition(e.value.as[YMap])).getOrElse(position.Position.ZERO)
    )
  }

  def parsePosition(m: YMap): position.Position = {
    new position.Position(parseNumber(m, "line").getOrElse(1), parseNumber(m, "column").getOrElse(0))
  }

  def parseNumber(m: YMap, field: String): Option[Int] = m.key(field).map(e => e.value.as[Int])

  def parseBoolean(m: YMap, field: String): Option[Boolean] = m.key(field).map(e => e.value.as[Boolean])

  def parseString(m: YMap, field: String): Option[String] = m.key(field).flatMap(e => e.value.asScalar.map(_.text))

  def readIdValue(e: YMapEntry): Option[String] = {
    e.value.as[YMap].key("@id").flatMap(_.value.asScalar).map(_.text)
  }
}

class ResultParser(map: YMap, rootUri: String, profile: Option[ValidationProfileWrapper]) extends JsonLDReader {

  implicit class URI(value: String) {
    val SHACL_ALIAS = "http://www.w3.org/ns/shacl#"

    def stripShacl: String = value.replace(SHACL_ALIAS, "")
  }

  lazy val node: String    = map.key("focusNode").flatMap(readIdValue).getOrElse("")
  lazy val message: String = map.key("resultMessage").flatMap(_.value.asScalar).map(_.text).getOrElse("")
  lazy val validationName: Option[String] = map
    .key("sourceShapeName")
    .flatMap(_.value.asScalar)
    .map(_.text)
  lazy val validationId: Option[String] =
    validationName
      .flatMap(name => {
        profile
          .flatMap(
            p =>
              p.validations()
                .find(v => v.name().contains(name))
                .map(_.getId))
      })
  lazy val level: String =
    map.key("resultSeverity").flatMap(s => readIdValue(s).map(_.stripShacl)).getOrElse("Violation")
  lazy val trace: Seq[TraceParser] =
    map.key("trace").map(_.value.as[Seq[YMap]]).getOrElse(Seq()).map(new TraceParser(_, rootUri, profile))

  def build(profileName: String): AlsValidationResult = {
    val stack = buildStack().reverse // More specific at the top
    val location = stack.headOption
      .map(_.location)
      .orElse(trace.map(_.location).headOption)
    val amfValidationResult = AMFValidationResult(
      message,
      level,
      node,
      None,
      validationId.getOrElse(profileName + validationId.map(id => s"#$id").getOrElse(profileName)),
      location.map(l => LexicalInformation(dtoToParserRange(l.range))),
      location.flatMap(r => if (r.uri == "") Some(rootUri) else Some(r.uri)),
      Unit
    )
    new AlsValidationResult(amfValidationResult, stack)
  }

  def buildStack(): Seq[DiagnosticRelatedInformation] = {
    if (trace.size > 1) Seq() else trace.headOption.map(_.buildStack()).getOrElse(Seq())
  }

  private def dtoToParserRange(core: Range): position.Range =
    position.Range(position.Position(core.start.line + 1, core.start.character),
                   position.Position(core.end.line + 1, core.end.character))
}

class TraceParser(map: YMap, rootUri: String, profile: Option[ValidationProfileWrapper]) extends JsonLDReader {

  lazy val range: Option[position.Range] =
    map.key("location").map(e => e.value.as[YMap]).flatMap(parseRange)

  lazy val location: Location =
    range
      .map(r => Location(uri, LspRangeConverter.toLspRange(r.toPositionRange)))
      .getOrElse(
        traceValue.subresult.headOption
          .flatMap(_.trace.headOption.map(_.location))
          .getOrElse(unknownLocation)
      )

  lazy val uri: String = map.key("uri").map(_.value.toString()).getOrElse(rootUri)

  lazy val traceValue: TraceValueParser =
    map.key("traceValue").map(_.value.as[YMap]).map(new TraceValueParser(_, rootUri, profile)).get

  lazy val resultPath: Option[String] = parseString(map, "resultPath")

  def buildStack(): Seq[DiagnosticRelatedInformation] = {
    traceValue
      .buildMessage(resultPath)
      .map(msg => {
        val related = DiagnosticRelatedInformation(location, msg)
        val stack   = traceValue.buildStack()
        related +: stack
      })
      .getOrElse(Seq.empty)
  }
}

class TraceValueParser(map: YMap, rootUri: String, profile: Option[ValidationProfileWrapper]) extends JsonLDReader {
  lazy val argument: Option[String]  = map.key("argument").map(_.value.value.toString)
  lazy val negated: Boolean          = parseBoolean(map, "negated").getOrElse(false)
  lazy val actual: Option[String]    = parseString(map, "actual")
  lazy val expected: Option[String]  = parseString(map, "expected")
  lazy val condition: Option[String] = parseString(map, "condition")
  lazy val subresult: Seq[ResultParser] =
    map
      .key("subResult")
      .map(e => e.value.as[Seq[YMap]])
      .map(s => s.map(new ResultParser(_, rootUri, profile)))
      .getOrElse(Seq())

  def buildStack(): Seq[DiagnosticRelatedInformation] =
    subresult.flatMap(s => s.buildStack())

  def buildMessage(resultPath: Option[String]): Option[String] =
    subresult.headOption.map(_.message).orElse(craftMessage(resultPath))

  def craftMessage(resultPath: Option[String]): Option[String] = {
    (resultPath, argument, actual) match {
      case (None, None, None) => None // We have nothing to build the message on
      case _ =>
        val builder  = StringBuilder.newBuilder
        val negative = if (negated) " not " else " "
        builder ++= "Error "
        argument.foreach(arg => builder ++= s"$arg unexpected$negative")
        actual.foreach(actual => {
          expected.foreach(expected => {
            val logicSign = condition.getOrElse("but got")
            builder ++= s"expected$negative$expected $logicSign actual (actual=$actual) "
          })
        })
        resultPath.foreach(p => builder ++= s"at $p")
        Some(builder.toString())
    }

  }
}
