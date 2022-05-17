package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.internal.annotations.ErrorDeclaration
import amf.core.internal.metamodel.domain.ShapeModel
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLJsonSchemaStyleDeclarationReferences(
    dialect: Dialect,
    ids: Seq[String],
    actualName: Option[String],
    yPart: YPartBranch,
    iriToPath: Map[String, String]
) {

  def resolve(dp: DeclarationProvider): Seq[RawSuggestion] = {
    val declarationsPath = dialect.documents().declarationsPath().option().map(_ + "/").getOrElse("")
    val routes = ids.flatMap { id =>
      dp.forNodeType(id).filter(n => !actualName.contains(n)).map { name =>
        nameForIri(id).fold(s"#/$name")(n => s"#/$declarationsPath$n/$name")
      }
    }
    AMLJsonSchemaStyleDeclarationReferences.resolveRoutes(routes, yPart)
  }

  def nameForIri(iri: String): Option[String] = {
    val finalIri =
      if (iri.contains("Shape")) ShapeModel.`type`.head.iri()
      else iri

    iriToPath.get(finalIri)
  }
}

object AMLJsonSchemaStyleDeclarationReferences extends AMLDeclarationReferences {
  override def id: String = "AMLJsonSchemaStyleDeclarationReferences"

  def applies(request: AmlCompletionRequest): Boolean = {
    request.yPartBranch.isValue && request.yPartBranch.parentEntryIs("$ref") && request.actualDialect
      .documents()
      .referenceStyle()
      .option()
      .forall(_ == ReferenceStyles.JSONSCHEMA) && isLocal(request.yPartBranch)
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
    val ids        = getObjectRangeIds(request)

    val mappings: Seq[NodeMapping] = request.actualDialect.declares.collect({ case n: NodeMapping => n })

    val iriToPath: Map[String, String] = request.actualDialect
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

    new AMLJsonSchemaStyleDeclarationReferences(request.actualDialect, ids, actualName, request.yPartBranch, iriToPath)
      .resolve(request.declarationProvider)
  }

  private def isLocal(yPart: YPartBranch) = yPart.stringValue.isEmpty || yPart.stringValue.startsWith("#")

  def resolveRoutes(routes: Seq[String], yPart: YPartBranch): Seq[RawSuggestion] = {
    val filtered = if (yPart.stringValue.isEmpty) routes else routes.filter(_.startsWith(yPart.stringValue))
    filtered
      .map(route => RawSuggestion(route, route, s"Reference to $route", Nil))
  }
}
