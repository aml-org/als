package org.mulesoft.als.common

import amf.core.parser
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfInstance
import org.scalatest.{Assertion, AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class ObjectInTreeTests extends AsyncFlatSpec with PlatformSecrets with Matchers with FileAssertionTest {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
  behavior of "Object in Tree finder"

  private def runTest(pos: Position,
                      expectedTypeIri: String,
                      expectedPropertyTerm: Option[String]): Future[Assertion] =
    objectInTree.map { fn =>
      val tree = fn(pos.toAmfPosition)
      expectedPropertyTerm.foreach { pt =>
        val fieldEntry = tree.fieldEntry.map(_.field.toString())
        fieldEntry should contain(pt)
      }
      expectedPropertyTerm match {
        case Some(pt) =>
          val fieldEntry = tree.fieldEntry.map(_.field.toString())
          fieldEntry should contain(pt)
        case None =>
          tree.fieldEntry should be(None)
      }
      tree.obj.meta match {
        case ddem: DialectDomainElementModel =>
          ddem.typeIri should contain(expectedTypeIri)
        case _ => fail("Not a DialectDomainElementModel")
      }
    }

  it should "identify a correct Root" in {
    val pos                  = Position(15, 0)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = None
    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a correct Declaration (final)" in {
    val pos                  = Position(15, 2)
    val expectedTypeIri      = "http://internal.namespace.com/Root" // todo: fix case
    val expectedPropertyTerm = None
    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a correct property (final)" in {
    val pos                  = Position(15, 6)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a correct property value (final)" in {
    val pos                  = Position(15, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a single child" in {
    val pos                  = Position(21, 4)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = Some("http://internal.namespace.com/y")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a first property" in {
    val pos                  = Position(3, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify a middle property" in {
    val pos                  = Position(4, 7)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify inside property value (array)" in {
    val pos                  = Position(5, 8)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify inside property value (scalar)" in {
    val pos                  = Position(5, 12)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify an array child" in {
    val pos                  = Position(5, 13)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property with multiple values" in {
    val pos                  = Position(23, 3)
    val expectedTypeIri      = "http://internal.namespace.com/Root"
    val expectedPropertyTerm = Some("http://internal.namespace.com/z")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property sublevel" in {
    val pos                  = Position(24, 10)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = Some("http://internal.namespace.com/a1")

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property sublevel with array" in {
    val pos                  = Position(26, 10)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  it should "identify root property sublevel incomplete" in {
    val pos                  = Position(28, 4)
    val expectedTypeIri      = "http://internal.namespace.com/A"
    val expectedPropertyTerm = None

    runTest(pos, expectedTypeIri, expectedPropertyTerm)
  }

  private def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/aml/$part"

  private val instance = AmfInstance(platform, Environment())
  private val initDialects: Future[Unit] =
    platform
      .resolve(uriTemplate("dialects/dialect1.yaml"))
      .map(_.stream.toString)
      .flatMap(
        dialectContent =>
          instance
            .init()
            .andThen {
              case _ => instance.alsAmlPlugin.registry.registerDialect(dialectContent)
          })

  private val objectInTree: Future[parser.Position => ObjectInTree] = for {
    _  <- initDialects
    bu <- instance.parse(uriTemplate("instances/instance1.yaml"))
  } yield {
    ObjectInTreeBuilder.fromUnit(bu.baseUnit, _)
  }
}
