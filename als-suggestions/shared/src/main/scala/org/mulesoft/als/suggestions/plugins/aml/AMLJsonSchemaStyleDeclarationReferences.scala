package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.domain.ShapeModel
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.common.ElementNameExtractor._
import org.mulesoft.als.common.YPartBranch
import org.yaml.model.{DoubleQuoteMark, NonMark, ScalarMark}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLJsonSchemaStyleDeclarationReferences(iriToNameMap: Map[String, String],
                                              ranges: Seq[String],
                                              actualName: Option[String],
                                              mark: Option[ScalarMark]) {

  def resolve(dp: DeclarationProvider, actualValue: String): Seq[RawSuggestion] = {
    val routes = ranges.flatMap { id =>
      dp.forNodeType(id).filter(n => !actualName.contains(n)).map { name =>
        nameForIri(id).fold(s"#/$name")(n => s"#/$n/$name")
      }
    }

    val filtered = if (actualValue.isEmpty) routes else routes.filter(_.startsWith(actualValue))
    filtered
      .map(r => mark.fold(r)(m => m.markText(r)))
      .map(route => RawSuggestion(route, route, s"Reference to $route", Nil, isKey = false, ""))
  }

  def nameForIri(iri: String): Option[String] = {
    val finalIri =
      if (iri.contains("Shape")) ShapeModel.`type`.head.iri()
      else iri
    iriToNameMap.get(finalIri)
  }
}

object AMLJsonSchemaStyleDeclarationReferences extends AMLDeclarationReferences {
  override def id: String = "AMLJsonSchemaStyleDeclarationReferences"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      val stringValue = request.yPartBranch.stringValue
      if (request.yPartBranch.isValue && request.yPartBranch.parentEntryIs("$ref") && request.actualDialect
            .documents()
            .referenceStyle()
            .option()
            .forall(_ == ReferenceStyles.JSONSCHEMA)) {
        if (isLocal(request.yPartBranch)) {
          val actualName = request.amfObject.elementIdentifier()

          val mappings = request.actualDialect.declares.collect({ case n: NodeMapping => n })
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

          val ids = getObjectRangeIds(request)
          val mark =
            if (request.yPartBranch.stringValue.isEmpty)
              request.yPartBranch.getMark.filter(_ != NonMark).orElse(Some(DoubleQuoteMark))
            else None
          new AMLJsonSchemaStyleDeclarationReferences(map, ids, actualName, mark)
            .resolve(request.declarationProvider, stringValue)

        } else Nil // remotes here?

      } else Nil
    }
  }

  private def isLocal(yPart: YPartBranch) = yPart.stringValue.isEmpty || yPart.stringValue.startsWith("#")
}
