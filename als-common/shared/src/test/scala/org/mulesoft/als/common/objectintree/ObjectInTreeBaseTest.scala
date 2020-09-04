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

  private val initDialects: Future[Unit] =
    platform
      .resolve(uriTemplate(dialectFile))
      .map(_.stream.toString)
      .flatMap(
        dialectContent =>
          instance
            .init()
            .flatMap(_ => instance.alsAmlPlugin.registry.registerDialect(dialectContent)))
      .flatMap(_ => Future.unit)

  private val eventualResult = instance.parse(uriTemplate(instanceFile))
  private val objectInTree: Future[parser.Position => ObjectInTree] =
    for {
      _      <- initDialects
      result <- eventualResult
    } yield {
      ObjectInTreeBuilder.fromUnit(result.baseUnit, _)
    }

  def runTest(pos: Position, expectedTypeIri: String, expectedPropertyTerm: Option[String]): Future[Assertion] = {
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
        case _ =>
          fail("Not a DialectDomainElementModel")
      }
    }
  }
}
