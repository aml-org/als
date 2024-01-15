package org.mulesoft.als.common.objectintree

import org.mulesoft.als.common.dtoTypes.Position
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.ExecutionContext

class ObjectInTreeMapsTests extends AsyncFlatSpec {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private val tester = ObjectInTreeBaseTest(
    "instances/instance2.yaml",
    "dialects/dialect2.yaml",
    newCachingLogic = true
  )
  behavior of "Object in Tree finder (dialect with Maps)"
  // 0 based position!

  it should "identify a correct Root" in {
    val pos                  = Position(8, 0)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a parent node according to indentation (subproperty)" in {
    val pos                  = Position(8, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a parent node according to indentation (inside field)" in {
    val pos                  = Position(8, 6)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None // it creates a virtual A inside the array (no AST)
    // Some("http://internal.namespace.com/a2")

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a parent node according to indentation (plain node)" in {
    val pos                  = Position(8, 4)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a parent node according to indentation (above root)" in {
    val pos                  = Position(8, 2)
    val expectedTypeIri      = "http://internal.namespace.com/Root" // todo: check
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  ignore should "identify a field entry, right of key (in map)" in {
    val pos                  = Position(15, 7)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a2") // todo: check

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a field entry on top of children" in {
    val pos                  = Position(16, 6)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a field entry between children" in {
    val pos                  = Position(21, 12)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a field entry breaking indentation" in {
    val pos                  = Position(21, 10)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None
    // todo: add check to ensure which level I'm in (should not be on the same level as `a1: name`)

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a field entry after children" in {
    val pos                  = Position(24, 12)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify parent inside empty Map value" in {
    val pos                  = Position(27, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify field on incomplete map" in {
    val pos                  = Position(31, 6)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify parent after incomplete map" in {
    val pos                  = Position(31, 4)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify parent according to indentation after incomplete map" in {
    val pos                  = Position(31, 2)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }
}
