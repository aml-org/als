package org.mulesoft.als.actions.codeactions

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionFactory, CodeActionRequestParams}
import org.mulesoft.als.common.{PlatformDirectoryResolver, WorkspaceEditSerializer}
import org.mulesoft.als.common.cache.UnitWithCaches
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.AmfElementDefaultVisitors
import org.mulesoft.amfintegration.{AmfInstance, AmfParseResult, ParserHelper}
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.scalatest.{Assertion, AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait BaseCodeActionTests extends AsyncFlatSpec with Matchers with FileAssertionTest {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  protected def relativeUri(element: String): String =
    s"file://als-actions/shared/src/test/resources/codeactions/$element"

  private def assertCodeActions(result: Seq[CodeAction], goldenPath: String): Future[Assertion] = {
    val value1   = result.headOption.flatMap(_.edit)
    val expected = WorkspaceEditSerializer(value1.getOrElse(WorkspaceEdit.empty)).serialize()

    for {
      tmp <- writeTemporaryFile(goldenPath)(expected)
      r   <- assertDifferences(tmp, goldenPath)
    } yield r
  }

  protected def runTest(elementUri: String,
                        range: PositionRange,
                        pluginFactory: CodeActionFactory,
                        golden: Option[String] = None,
                        definedBy: Option[String] = None): Future[Assertion] =
    for {
      params <- buildParameter(elementUri, range, definedBy)
      result <- {
        val plugin = pluginFactory(params)
        plugin.isApplicable should be(true)
        plugin.run(params)
      }
      r <- assertCodeActions(result.map(_.toCodeAction(true)),
                             relativeUri(golden.getOrElse(s"$elementUri.golden.yaml")))
    } yield r

  protected def runTestNotApplicable(elementUri: String,
                                     range: PositionRange,
                                     pluginFactory: CodeActionFactory): Future[Assertion] =
    for {
      params <- buildParameter(elementUri, range)
    } yield pluginFactory(params).isApplicable should be(false)

  protected def parseElement(elementUri: String, definedBy: Option[String] = None): Future[AmfParseResult] = {

    val helper = new ParserHelper(platform, AmfInstance.default)
    for {

      _ <- definedBy.fold(Future.unit)(db => helper.parse(relativeUri(db)).map(_ => Unit))
      r <- helper.parse(relativeUri(elementUri))
    } yield r

  }

  protected def buildParameter(elementUri: String,
                               range: PositionRange,
                               definedBy: Option[String] = None): Future[CodeActionRequestParams] = {
    val amfInstance = AmfInstance.default
    amfInstance
      .init()
      .flatMap(
        _ =>
          parseElement(elementUri, definedBy)
            .map(bu => buildParameter(elementUri, bu, range, amfInstance)))
  }

  case class PreCodeActionRequestParams(amfResult: AmfParseResult, uri: String, relationShip: Seq[RelationshipLink]) {
    def buildParam(range: PositionRange,
                   activeFile: Option[String],
                   amfInstance: AmfInstance): CodeActionRequestParams = {
      val cu: DummyCompilableUnit = buildCU(activeFile)
      CodeActionRequestParams(
        cu.unit.location().getOrElse(relativeUri(uri)),
        range,
        cu.unit,
        cu.tree,
        cu.yPartBranch,
        amfResult.definedBy,
        AlsConfiguration(),
        relationShip,
        dummyTelemetryProvider,
        "",
        amfInstance,
        new PlatformDirectoryResolver(platform)
      )
    }

    private def buildCU(activeFile: Option[String]) = {
      activeFile
        .flatMap { af =>
          val relativaf = relativeUri(af)
          amfResult.baseUnit.references.find(r => r.location().getOrElse(r.id) == relativaf).map { unit =>
            DummyCompilableUnit(unit, amfResult.definedBy)
          }
        }
        .getOrElse(DummyCompilableUnit(amfResult.baseUnit, amfResult.definedBy))
    }
  }
  protected def buildPreParam(uri: String, result: AmfParseResult): PreCodeActionRequestParams = {
    val visitors = AmfElementDefaultVisitors.build(result.baseUnit)
    visitors.applyAmfVisitors(result.baseUnit)
    val visitors1: Seq[RelationshipLink] = visitors.getRelationshipsFromVisitors
    PreCodeActionRequestParams(result, uri, visitors1)
  }

  protected def buildParameter(uri: String,
                               result: AmfParseResult,
                               range: PositionRange,
                               amfInstance: AmfInstance): CodeActionRequestParams = {
    val cu       = DummyCompilableUnit(result.baseUnit, result.definedBy)
    val visitors = AmfElementDefaultVisitors.build(cu.unit)
    visitors.applyAmfVisitors(cu.unit)
    val visitors1: Seq[RelationshipLink] = visitors.getRelationshipsFromVisitors
    CodeActionRequestParams(
      cu.unit.location().getOrElse(relativeUri(uri)),
      range,
      cu.unit,
      cu.tree,
      cu.yPartBranch,
      result.definedBy,
      AlsConfiguration(),
      visitors1,
      dummyTelemetryProvider,
      "",
      amfInstance,
      new PlatformDirectoryResolver(platform)
    )
  }

  protected val dummyTelemetryProvider: TelemetryProvider = new TelemetryProvider {
    override protected def addTimedMessage(code: String,
                                           messageType: MessageTypes,
                                           msg: String,
                                           uri: String,
                                           uuid: String): Unit = {} // do nothing

    override def addErrorMessage(code: String, msg: String, uri: String, uuid: String): Unit = {} // do nothing
  }
}

case class DummyCompilableUnit(unit: BaseUnit, override protected val definedBy: Dialect) extends UnitWithCaches
