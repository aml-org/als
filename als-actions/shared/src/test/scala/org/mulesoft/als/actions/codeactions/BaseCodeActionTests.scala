package org.mulesoft.als.actions.codeactions

import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionFactory, CodeActionRequestParams}
import org.mulesoft.als.common.cache.UnitWithCaches
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.amfintegration.{AmfInstance, AmfParseResult, ParserHelper}
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.scalatest.{Assertion, AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait BaseCodeActionTests extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  protected def relativeUri(element: String): String =
    s"file://als-actions/shared/src/test/resources/codeactions/$element"

  protected def runTest(elementUri: String,
                        range: PositionRange,
                        dialect: Option[Dialect],
                        pluginFactory: CodeActionFactory,
                        expected: Seq[CodeAction]): Future[Assertion] =
    for {
      params <- buildParameter(elementUri, range, dialect)
      result <- {
        val plugin = pluginFactory(params)
        plugin.isApplicable should be(true)
        plugin.run(params)
      }
    } yield {
      result should be(expected)
    }

  protected def parseElement(elementUri: String): Future[AmfParseResult] =
    new ParserHelper(platform, AmfInstance.default)
      .parse(relativeUri(elementUri))

  protected def buildParameter(elementUri: String,
                               range: PositionRange,
                               dialect: Option[Dialect]): Future[CodeActionRequestParams] =
    parseElement(elementUri)
      .map(bu => DummyCompilableUnit(bu.baseUnit))
      .map { cu =>
        CodeActionRequestParams(cu.unit.location().getOrElse(relativeUri(elementUri)),
                                range,
                                cu.unit,
                                cu.tree,
                                cu.yPartBranch,
                                dialect,
                                AlsConfiguration(),
                                dummyTelemetryProvider,
                                "")
      }

  protected val dummyTelemetryProvider: TelemetryProvider =
    (_: String, _: MessageTypes, _: String, _: String, _: String) => {
      /* do nothing */
    }
}

case class DummyCompilableUnit(unit: BaseUnit) extends UnitWithCaches
