package org.mulesoft.als.common.objectintree

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.declarations.DeclarationCreator
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.scalatest.compatible.Assertion
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.{ExecutionContext, Future}

class DeclarationCreatorTests extends AsyncFlatSpec with DeclarationCreator {
  override implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  protected def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/apis/$part"

  behavior of "DeclarationCreator - afterInfoNode"

  it should "position after latter of name or version in RAML API" in {
    runTest("api.raml", Position(6, 0))
  }

  it should "not find a position if there is no name or version in RAML API" in {
    runTest("api-empty.raml")
  }

  it should "position after usage in RAML Library" in {
    runTest("library.raml", Position(4, 0))
  }

  it should "position after info node in OAS" in {
    runTest("api.yaml", Position(7, 0))
  }

  private def runTest(file: String): Future[Assertion] =
    baseUnit(file)
      .map { baseUnit =>
        assert(afterInfoNode(baseUnit, isJson = false).isEmpty)
      }

  private def runTest(file: String, position: Position): Future[Assertion] =
    baseUnit(file)
      .map { baseUnit =>
        assert(afterInfoNode(baseUnit, isJson = false).contains(position))
      }

  private def baseUnit(file: String): Future[BaseUnit] =
    for {
      state  <- EditorConfiguration().getState
      result <- ALSConfigurationState(state, EmptyProjectConfigurationState, None).parse(uriTemplate(file))
    } yield {
      result.result.baseUnit
    }
}
