package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.domain.NodeMapping
import amf.apicontract.internal.metamodel.domain.templates.{ResourceTypeModel, TraitModel}
import amf.apicontract.internal.metamodel.domain.{MessageModel, RequestModel, ResponseModel}
import amf.core.client.scala.model.domain.{AmfElement, AmfObject, Linkable}
import amf.core.internal.annotations.ErrorDeclaration
import amf.core.internal.metamodel.domain.templates.AbstractDeclarationModel
import amf.core.internal.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.shapes.internal.domain.metamodel.AnyShapeModel
import amf.shapes.internal.domain.metamodel.operations.AbstractResponseModel
import org.mulesoft.als.common.ASTPartBranch
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLJsonSchemaStyleDeclarationReferences(
                                               documentDefinition: DocumentDefinition,
                                               ids: Seq[String],
                                               actualName: Option[String],
                                               astPartBranch: ASTPartBranch,
                                               iriToPath: Map[String, String]
) {

  def resolve(dp: DeclarationProvider): Seq[RawSuggestion] = {
    val declarationsPath = documentDefinition.documents().declarationsPath().option().map(_ + "/").getOrElse("")
    val routes = ids.flatMap { id =>
      dp.forNodeType(id).filter(n => !actualName.contains(n)).map { name =>
        nameForIri(id).fold(s"#/$name")(n => s"#/$declarationsPath$n/$name")
      }
    }
    AMLJsonSchemaStyleDeclarationReferences.resolveRoutes(routes, astPartBranch)
  }

  def nameForIri(iri: String): Option[String] = {
    val finalIri =
      if (iri.contains("Shape")) ShapeModel.`type`.head.iri()
      else iri

    iriToPath.get(finalIri)
  }
}

trait AbstractAMLJsonSchemaStyleDeclarationReferences extends AMLDeclarationReferences {
  override def id: String = "AMLJsonSchemaStyleDeclarationReferences"

  def applies(request: AmlCompletionRequest): Boolean = {
    request.astPartBranch.isValue && request.astPartBranch.parentEntryIs("$ref") && request.actualDocumentDefinition
      .documents()
      .referenceStyle()
      .option()
      .forall(_ == ReferenceStyles.JSONSCHEMA) && isLocal(request.astPartBranch)
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (applies(request)) suggest(request)
      else Nil // remotes here?
    }
  }

  private def errorParentName(request: AmlCompletionRequest): Option[String] = {
    if (request.amfObject.isInstanceOf[ErrorDeclaration[_]])
      request.branchStack.headOption.flatMap(_.elementIdentifier())
    else None
  }

  def suggest(request: AmlCompletionRequest): Seq[RawSuggestion] = {
    val actualName = request.amfObject.elementIdentifier().orElse(errorParentName(request))
    val ids =
      request.amfObject match {
        case link: Linkable if containsAstInStack(link, request.branchStack) =>
          Seq(AnyShapeModel.`type`.head.iri())
        case _ => getObjectRangeIds(request)
      }

    val mappings: Seq[NodeMapping] = request.actualDocumentDefinition.declares.collect({ case n: NodeMapping => n })

    val iriToPath: Map[String, String] = request.actualDocumentDefinition
      .documents()
      .root()
      .declaredNodes()
      .flatMap(dn =>
        mappings
          .find(_.id == dn.mappedNode().value())
          .map(_.nodetypeMapping.value())
          .map(iri => iri -> dn.name().value())
      )
      .toMap

    new AMLJsonSchemaStyleDeclarationReferences(
      request.actualDocumentDefinition,
      ids,
      actualName,
      request.astPartBranch,
      iriToPath
    )
      .resolve(request.declarationProvider)
  }

  private def containsAstInStack(link: Linkable, stack: Seq[AmfObject]): Boolean =
    stack.exists(p => link.linkTarget.exists(t => sameAst(p, t)))

  private def sameAst(a: AmfElement, b: AmfElement) =
    (for {
      aAst <- a.annotations.yPart()
      bAst <- b.annotations.yPart()
    } yield bAst == aAst).getOrElse(false)

  private def isLocal(astPartBranch: ASTPartBranch) =
    astPartBranch.stringValue.isEmpty || astPartBranch.stringValue.startsWith("#")

  def resolveRoutes(routes: Seq[String], astPartBranch: ASTPartBranch): Seq[RawSuggestion] = {
    val filtered =
      if (astPartBranch.stringValue.isEmpty) routes else routes.filter(_.startsWith(astPartBranch.stringValue))
    filtered
      .map(route => RawSuggestion(route, route, s"Reference to $route", Nil))
  }
}
object AMLJsonSchemaStyleDeclarationReferences extends AbstractAMLJsonSchemaStyleDeclarationReferences
object Async2AMLJsonSchemaStyleDeclarationReferences extends AbstractAMLJsonSchemaStyleDeclarationReferences {
  override protected val exceptions: Seq[String] = Seq(
    DomainElementModel.`type`.head.iri(),
    // workaround because for async2 an incomplete message reference wraps around a response
    ResponseModel.`type`.head.iri(),
    AbstractResponseModel.`type`.head.iri(),
    // handled by RamlAbstractDeclarationReference:
    AbstractDeclarationModel.`type`.head.iri(),
    TraitModel.`type`.head.iri(),
    ResourceTypeModel.`type`.head.iri()
  )

  // publish and subscribe are both messages (use components/messages)
  override protected def getObjectRangeIds(params: AmlCompletionRequest): Seq[String] =
    super.getObjectRangeIds(params).map {
      case id if RequestModel.`type`.headOption.map(_.iri()).contains(id) => MessageModel.`type`.head.iri()
      case id                                                             => id
    }
}
