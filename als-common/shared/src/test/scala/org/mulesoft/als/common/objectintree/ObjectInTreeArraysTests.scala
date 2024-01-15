package org.mulesoft.als.common.objectintree

import org.mulesoft.als.common.dtoTypes.Position
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.ExecutionContext

class ObjectInTreeArraysTests extends AsyncFlatSpec {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private def tester = ObjectInTreeBaseTest(
    "instances/instance1.yaml",
    "dialects/dialect1.yaml",
    newCachingLogic = true
  )

  behavior of "Object in Tree finder (dialect with Arrays)"

  it should "identify a correct Root" in {
    val pos                  = Position(15, 0)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a correct Declaration (final)" in {
    val pos                  = Position(15, 2)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a correct property (final)" in {
    val pos                  = Position(15, 6)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a correct property value (final)" in {
    val pos                  = Position(15, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a single child" in {
    val pos                  = Position(21, 4)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = Some("http://internal.namespace.com/y")

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a first property" in {
    val pos                  = Position(3, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  ignore should "identify a middle property" in {
    val pos                  = Position(4, 7)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify inside property value (array)" in {
    val pos                  = Position(5, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify inside property value (scalar)" in {
    val pos                  = Position(5, 12)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  ignore should "identify root property with multiple values" in {
    val pos                  = Position(23, 3)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = Some("http://internal.namespace.com/z")
    // todo: check that there is no A created

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property sublevel" in {
    val pos                  = Position(24, 10)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property sublevel with array" in {
    val pos                  = Position(26, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None
    // todo: check this is not the declared (it's a Linkable)

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property sublevel incomplete" in {
    val pos                  = Position(28, 4)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    tester.runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }
}
