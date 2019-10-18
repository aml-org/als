package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.domain.ShapeModel
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.common.ElementNameExtractor._
import org.mulesoft.als.common.YPartBranch
import org.yaml.model.{DoubleQuoteMark, NoMark, ScalarMark}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLJsonSchemaStyleDeclarationReferences(dialect: Dialect,
                                              ranges: Seq[String],
                                              actualName: Option[String],
                                              yPart: YPartBranch,
                                              iriToPath: Map[String, String]) {

  val mark: Option[ScalarMark] =
    if (yPart.stringValue.isEmpty) yPart.getMark.filter(_ != NoMark).orElse(Some(DoubleQuoteMark))
    else if (yPart.isJson) yPart.getMark
    else None

  def resolve(dp: DeclarationProvider): Seq[RawSuggestion] = {
    val routes = ranges.flatMap { id =>
      dp.forNodeType(id).filter(n => !actualName.contains(n)).map { name =>
        nameForIri(id).fold(s"#/$name")(n => s"#/$n/$name")
      }
    }

    val filtered = if (yPart.stringValue.isEmpty) routes else routes.filter(_.startsWith(yPart.stringValue))
    filtered
      .map(r =>
        mark.fold(if (yPart.isJson) DoubleQuoteMark.markText(r) else r)(m => if (yPart.isJson) r else m.markText(r)))
      .map(route => RawSuggestion(route, route, s"Reference to $route", Nil, ""))
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
    val stringValue = request.yPartBranch.stringValue
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

  def suggest(request: AmlCompletionRequest): Seq[RawSuggestion] = {
    val actualName = request.amfObject.elementIdentifier()
    val ids        = getObjectRangeIds(request)

    val mappings: Seq[NodeMapping] = request.actualDialect.declares.collect({ case n: NodeMapping => n })

    val map: Map[String, String] = request.actualDialect
      .documents()
      .root()
      .declaredNodes()
      .flatMap(
        dn =>
          mappings
            .find(_.id == dn.mappedNode().value())
            .map(_.nodetypeMapping.value())
            .map(iri => iri -> dn.name().value()))
      .toMap

    new AMLJsonSchemaStyleDeclarationReferences(request.actualDialect, ids, actualName, request.yPartBranch, map)
      .resolve(request.declarationProvider)
  }

  private def isLocal(yPart: YPartBranch) = yPart.stringValue.isEmpty || yPart.stringValue.startsWith("#")
}
