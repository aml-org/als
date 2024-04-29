package org.mulesoft.als.actions.codeactions

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionFactory, CodeActionRequestParams}
import org.mulesoft.als.common.cache.UnitWithCaches
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.common.{PlatformDirectoryResolver, WorkspaceEditSerializer}
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  AmfParseResult,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.AmfElementDefaultVisitors
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.codeactions.CodeAction
import org.scalatest.Assertion
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

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

  protected def runTest(
      elementUri: String,
      range: PositionRange,
      pluginFactory: CodeActionFactory,
      golden: Option[String] = None,
      definedBy: Option[String] = None
  ): Future[Assertion] =
    for {
      params <- buildParameter(elementUri, range, definedBy)
      result <- {
        val plugin = pluginFactory(params)
        plugin.isApplicable should be(true)
        plugin.run(params)
      }
      r <- assertCodeActions(
        result.map(_.toCodeAction(true)),
        relativeUri(golden.getOrElse(s"$elementUri.golden.yaml"))
      )
    } yield r

  protected def runTestNotApplicable(
      elementUri: String,
      range: PositionRange,
      pluginFactory: CodeActionFactory
  ): Future[Assertion] =
    for {
      params <- buildParameter(elementUri, range)
    } yield pluginFactory(params).isApplicable should be(false)

  protected def parseElement(
      elementUri: String,
      definedBy: Option[String] = None,
      editorConfiguration: EditorConfiguration
  ): Future[AmfParseResult] = {
    definedBy.foreach(d => editorConfiguration.withDialect(relativeUri(d)))
    for {
      editor <- editorConfiguration.getState
      state  <- Future(ALSConfigurationState(editor, EmptyProjectConfigurationState, None))
      r      <- state.parse(relativeUri(elementUri))
    } yield r
  }

  protected def buildParameter(
      elementUri: String,
      range: PositionRange,
      definedBy: Option[String] = None
  ): Future[CodeActionRequestParams] = {
    val configuration = EditorConfiguration()
    for {
      bu    <- parseElement(elementUri, definedBy, configuration)
      state <- configuration.getState
    } yield {
      val alsConfigurationState = ALSConfigurationState(state, EmptyProjectConfigurationState, None)
      buildParameter(elementUri, bu, range, alsConfigurationState)
    }
  }

  case class PreCodeActionRequestParams(amfResult: AmfParseResult, uri: String, relationShip: Seq[RelationshipLink]) {
    def buildParam(
        range: PositionRange,
        activeFile: Option[String],
        alsConfigurationState: ALSConfigurationState
    ): CodeActionRequestParams = {
      val cu: DummyCompilableUnit = buildCU(activeFile)
      CodeActionRequestParams(
        cu.unit.location().getOrElse(relativeUri(uri)),
        range,
        cu.unit,
        cu.tree,
        cu.astPartBranch,
        amfResult.definedBy,
        AlsConfiguration(),
        relationShip,
        alsConfigurationState,
        "",
        new PlatformDirectoryResolver(platform)
      )
    }

    private def buildCU(activeFile: Option[String]) = {
      activeFile
        .flatMap { af =>
          val relativaf = relativeUri(af)
          amfResult.result.baseUnit.references.find(r => r.location().getOrElse(r.id) == relativaf).map { unit =>
            DummyCompilableUnit(unit, amfResult.definedBy)
          }
        }
        .getOrElse(DummyCompilableUnit(amfResult.result.baseUnit, amfResult.definedBy))
    }
  }
  protected def buildPreParam(uri: String, result: AmfParseResult): PreCodeActionRequestParams = {
    val visitors = AmfElementDefaultVisitors.build(result.result.baseUnit)
    visitors.applyAmfVisitors(result.result.baseUnit, result.context)
    val visitors1: Seq[RelationshipLink] = visitors.getRelationshipsFromVisitors
    PreCodeActionRequestParams(result, uri, visitors1)
  }

  protected def buildParameter(
      uri: String,
      result: AmfParseResult,
      range: PositionRange,
      alsConfigurationState: ALSConfigurationState
  ): CodeActionRequestParams = {
    val cu       = DummyCompilableUnit(result.result.baseUnit, result.definedBy)
    val visitors = AmfElementDefaultVisitors.build(cu.unit)
    visitors.applyAmfVisitors(cu.unit, alsConfigurationState.amfParseContext)
    val visitors1: Seq[RelationshipLink] = visitors.getRelationshipsFromVisitors
    CodeActionRequestParams(
      cu.unit.location().getOrElse(relativeUri(uri)),
      range,
      cu.unit,
      cu.tree,
      cu.astPartBranch,
      result.definedBy,
      AlsConfiguration(),
      visitors1,
      alsConfigurationState,
      "",
      new PlatformDirectoryResolver(platform)
    )
  }
}

case class DummyCompilableUnit(unit: BaseUnit, override protected val definedBy: Dialect) extends UnitWithCaches
