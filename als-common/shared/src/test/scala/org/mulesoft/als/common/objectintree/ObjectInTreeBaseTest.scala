package org.mulesoft.als.common.objectintree

import amf.core.parser
import amf.core.unsafe.PlatformSecrets
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{ObjectInTree, ObjectInTreeBuilder}
import org.mulesoft.amfintegration.AmfInstance
import org.scalatest.{Assertion, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectInTreeBaseTest(instanceFile: String, dialectFile: String) extends PlatformSecrets with Matchers {
  protected def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/aml/$part"

  private val instance = AmfInstance.default

  private val initDialects: Future[Unit] = for {
    dialectContent <- platform.resolve(uriTemplate(dialectFile)).map(_.stream.toString)
    _              <- instance.init()
    _              <- instance.alsAmlPlugin.registry.registerDialect(dialectContent)
  } yield {}

  private val eventualResult = initDialects.flatMap(_ => instance.parse(uriTemplate(instanceFile)))

  private val objectInTree: Future[parser.Position => ObjectInTree] =
    eventualResult.map(result => ObjectInTreeBuilder.fromUnit(result.baseUnit, _))

  def runTest(pos: Position, expectedTypeIri: String, expectedPropertyTerm: Option[String]): Future[Assertion] = {
    objectInTree.map { fn =>
      val tree = fn(pos.toAmfPosition)
      assertPropertyTerm(expectedPropertyTerm, tree)
      assertTypeIri(expectedTypeIri, tree)
    }
  }

  private def assertTypeIri(expectedTypeIri: String, tree: ObjectInTree) =
    tree.obj.meta match {
      case ddem: DialectDomainElementModel =>
        ddem.typeIri should contain(expectedTypeIri)
      case _ =>
        fail("Not a DialectDomainElementModel")
    }

  private def assertPropertyTerm(expectedPropertyTerm: Option[String], tree: ObjectInTree) =
    expectedPropertyTerm match {
      case Some(pt) =>
        val fieldEntry = tree.fieldEntry.map(_.field.toString())
        fieldEntry should contain(pt)
      case None =>
        tree.fieldEntry should be(None)
    }
}
