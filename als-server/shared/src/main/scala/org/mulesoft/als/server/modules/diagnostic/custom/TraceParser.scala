package org.mulesoft.als.server.modules.diagnostic.custom

import amf.core.client.common.position
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import amf.custom.validation.client.scala.report.model.{OpaLocation, OpaResult, OpaTrace, OpaTraceValue}
import amf.custom.validation.internal.report.parser.AMFValidationOpaAdapter
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.diagnostic.AlsValidationResult
import org.mulesoft.amfintegration.ParserRangeImplicits._
import org.mulesoft.lsp.feature.common.{Location, Range}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation
class TraceParser(trace: OpaTrace, rootUri: String) {

  lazy val location: Location = {
    trace.location
      .flatMap(LocationParser.parse(_, rootUri))
      .getOrElse(
        traceValue.subresult.headOption
          .flatMap(_.trace.headOption.map(_.location))
          .getOrElse(unknownLocation)
      )
  }

  val unknownLocation: Location =
    Location(rootUri, LspRangeConverter.toLspRange(position.Range.NONE.toPositionRange))

  lazy val uri: String = {
    val l: String = trace.location.flatMap(_.location).getOrElse("")
    if (l.isEmpty) rootUri else l
  }

  lazy val traceValue: TraceValueParser = new TraceValueParser(trace.traceValue.head, rootUri)

  lazy val resultPath: Option[String] = trace.resultPath

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

class TraceValueParser(traceValue: OpaTraceValue, rootUri: String) {
  lazy val argument: Option[String]     = traceValue.argument
  lazy val negated: Boolean             = traceValue.negated
  lazy val actual: Option[String]       = traceValue.actual
  lazy val expected: Option[String]     = traceValue.expected
  lazy val condition: Option[String]    = traceValue.condition
  lazy val subresult: Seq[ResultParser] = traceValue.subResult.map(new ResultParser(_, rootUri))

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

class ResultParser(opaResult: OpaResult, rootUri: String) {

  implicit class URI(value: String) {
    val SHACL_ALIAS = "http://www.w3.org/ns/shacl#"

    def stripShacl: String = value.replace(SHACL_ALIAS, "")
  }

  lazy val node: String                   = opaResult.node
  lazy val message: String                = opaResult.message
  lazy val validationName: Option[String] = opaResult.validationName
  lazy val validationId: Option[String]   = opaResult.validationId
  lazy val level: String                  = opaResult.level
  lazy val location: Option[Location]     = opaResult.location.flatMap(LocationParser.parse(_, rootUri))
  lazy val trace: Seq[TraceParser]        = opaResult.trace.map(new TraceParser(_, rootUri))

  def build(profileName: String): AlsValidationResult = {

    val stack: Seq[DiagnosticRelatedInformation] = buildStack().reverse // More specific at the top
    val resultLocation = stack.headOption
      .map(_.location)
      .orElse(location)
      .orElse(trace.map(_.location).headOption)
    val amfValidationResult = AMFValidationResult(
      message,
      level,
      node,
      None,
      validationId.getOrElse(profileName + validationId.map(id => s"#$id").getOrElse(profileName)),
      resultLocation.map(l => LexicalInformation(dtoToParserRange(l.range))),
      resultLocation.flatMap(r => if (r.uri == "" || r.uri == "unknown") Some(rootUri) else Some(r.uri)),
      Unit
    )

    new AlsValidationResult(amfValidationResult, stack)
  }

  def buildStack(): Seq[DiagnosticRelatedInformation] = {
    if (trace.size > 1) Seq() else trace.headOption.map(_.buildStack()).getOrElse(Seq())
  }

  private def dtoToParserRange(core: Range): position.Range =
    position.Range(
      position.Position(core.start.line + 1, core.start.character),
      position.Position(core.end.line + 1, core.end.character)
    )
}

object LocationParser {
  def parse(loc: OpaLocation, rootUri: String): Option[Location] = {
    rangeFromLocation(loc)
      .map(r => {
        val locUri: String = loc.location.getOrElse("")
        val uri            = if (locUri.isEmpty) rootUri else locUri
        Location(uri, LspRangeConverter.toLspRange(r.toPositionRange))
      })
  }

  def rangeFromLocation(l: OpaLocation): Option[position.Range] = {
    l.range.map(AMFValidationOpaAdapter.adaptRange)
  }
}
