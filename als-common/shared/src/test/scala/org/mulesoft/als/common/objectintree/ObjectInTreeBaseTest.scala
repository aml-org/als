package org.mulesoft.als.common.objectintree

import amf.aml.client.scala.model.document.Dialect
import amf.aml.internal.metamodel.domain.DialectDomainElementModel
import amf.core.client.common.position.{Position => AmfPosition}
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, ObjectInTree, ObjectInTreeBuilder}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{AmfConfigurationWrapper, AmfParseResult}
import org.scalatest.{Assertion, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectInTreeBaseTest(instanceFile: String, dialectFile: String) extends PlatformSecrets with Matchers {
  protected def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/aml/$part"

  private val instance = AmfConfigurationWrapper()

  /**
    * parses and registers a Dialect
    */
  private val initDialects: Future[Dialect] =
    instance
      .init()
      .flatMap(
        i =>
          i.parse(uriTemplate(dialectFile))
            .map(r =>
              r.result.baseUnit match {
                case d: Dialect =>
                  instance.registerDialect(d)
                  d
                case _ => fail(s"Expected Dialect: ${uriTemplate(dialectFile)}")
            }))

  private val eventualResult: Future[AmfParseResult] = initDialects
    .flatMap(_ => instance.parse(uriTemplate(instanceFile)))

  private def fn(pos: AmfPosition, result: AmfParseResult, dialect: Dialect): ObjectInTree =
    ObjectInTreeBuilder.fromUnit(result.result.baseUnit,
                                 result.result.baseUnit.identifier,
                                 dialect,
                                 NodeBranchBuilder.build(result.result.baseUnit, pos, isJson = false))

  private def objectInTree(position: Position): Future[AmfPosition => ObjectInTree] =
    for {
      dialect <- initDialects
      result  <- eventualResult
    } yield fn(_, result, dialect)

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
        val fieldEntry = tree.fieldValue.map(_.field.toString())
        fieldEntry should contain(pt)
      case None =>
        tree.fieldValue should be(None)
    }

  def runTest(pos: Position, expectedTypeIri: String, expectedPropertyTerm: Option[String]): Future[Assertion] = {
    objectInTree(pos).map { fn =>
      val tree = fn(pos.toAmfPosition)
      assertPropertyTerm(expectedPropertyTerm, tree)
      assertTypeIri(expectedTypeIri, tree)
    }
  }
}
