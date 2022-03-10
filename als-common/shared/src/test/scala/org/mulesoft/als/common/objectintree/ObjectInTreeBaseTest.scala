package org.mulesoft.als.common.objectintree

import amf.aml.client.scala.model.document.Dialect
import amf.aml.internal.metamodel.domain.DialectDomainElementModel
import amf.core.client.common.position.{Position => AmfPosition}
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, ObjectInTree, ObjectInTreeBuilder}
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  AmfParseResult,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.scalatest.{Assertion, Matchers}

import scala.concurrent.{ExecutionContext, Future}

case class ObjectInTreeBaseTest(instanceFile: String, dialectFile: String) extends PlatformSecrets with Matchers {
  protected def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/aml/$part"

  implicit val ec: ExecutionContext = ExecutionContext.global

  private val global: EditorConfiguration = EditorConfiguration().withDialect(uriTemplate(dialectFile))

  private val eventualResult: Future[AmfParseResult] = {
    for {
      s      <- global.getState
      result <- ALSConfigurationState(s, EmptyProjectConfigurationState, None).parse(uriTemplate(instanceFile))
    } yield result
  }
  private def fn(pos: AmfPosition, result: AmfParseResult, dialect: Dialect): ObjectInTree =
    ObjectInTreeBuilder.fromUnit(result.result.baseUnit,
                                 result.result.baseUnit.identifier,
                                 dialect,
                                 NodeBranchBuilder.build(result.result.baseUnit, pos, isJson = false))

  private def objectInTree(): Future[AmfPosition => ObjectInTree] =
    for {
      dialect <- global.getState.map(_.dialects.find(_.location().exists(_.contains(dialectFile))))
      result  <- eventualResult
    } yield fn(_, result, dialect.get)

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
    objectInTree().map { fn =>
      val tree = fn(pos.toAmfPosition)
      assertPropertyTerm(expectedPropertyTerm, tree)
      assertTypeIri(expectedTypeIri, tree)
    }
  }
}
