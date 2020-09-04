package org.mulesoft.als.common.objectintree

import amf.core.parser
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import org.mulesoft.als.common.{ObjectInTree, ObjectInTreeBuilder}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfInstance
import org.scalatest.{Assertion, Matchers}

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

trait ObjectInTreeBaseTest extends PlatformSecrets with Matchers {
  protected def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/aml/$part"

  val instanceFile: String
  val dialectFile: String

  private val objectInTree: Future[parser.Position => ObjectInTree] = {
    val instance = AmfInstance(platform, Environment())
    val initDialects: Future[Unit] =
      platform
        .resolve(uriTemplate(dialectFile))
        .map(_.stream.toString)
        .flatMap(
          dialectContent =>
            instance
              .init()
              .andThen {
                case _ => instance.alsAmlPlugin.registry.registerDialect(dialectContent)
            })
    for {
      _  <- initDialects
      bu <- instance.parse(uriTemplate(instanceFile))
    } yield {
      ObjectInTreeBuilder.fromUnit(bu.baseUnit, _)
    }
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
        case _ => fail("Not a DialectDomainElementModel")
      }
    }
  }
}
