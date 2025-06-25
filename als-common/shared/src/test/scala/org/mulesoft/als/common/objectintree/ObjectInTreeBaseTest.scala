package org.mulesoft.als.common.objectintree

import amf.aml.client.scala.model.document.Dialect
import amf.aml.internal.metamodel.domain.DialectDomainElementModel
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, ObjectInTree, ObjectInTreeBuilder}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, AmfParseResult, DocumentDefinition, EditorConfiguration, EmptyProjectConfigurationState}
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.scalatest.compatible.Assertion
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

case class ObjectInTreeBaseTest(instanceFile: String, dialectFile: String) extends AlsPlatformSecrets with Matchers {
  protected def uriTemplate(part: String) = s"file://als-common/shared/src/test/resources/aml/$part"

  implicit val ec: ExecutionContext = ExecutionContext.global

  private val global: EditorConfiguration = EditorConfiguration().withDialect(uriTemplate(dialectFile))

  private val eventualResult: Future[AmfParseResult] = {
    for {
      s <- global.getState
      result <- ALSConfigurationState(s, EmptyProjectConfigurationState, None).parse(
        uriTemplate(instanceFile),
        asMain = true
      )
    } yield result
  }
  private def fn(pos: AmfPosition, result: AmfParseResult, documentDefinition: DocumentDefinition): ObjectInTree =
    ObjectInTreeBuilder.fromUnit(
      result.result.baseUnit,
      result.result.baseUnit.identifier,
      documentDefinition,
      NodeBranchBuilder.build(result.result.baseUnit, pos, strict = false)
    )

  private def objectInTree(): Future[AmfPosition => ObjectInTree] =
    for {
      dialect <- global.getState.map(_.dialects.find(_.location().exists(_.contains(dialectFile))))
      result  <- eventualResult
    } yield fn(_, result, DocumentDefinition(dialect.get))

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
