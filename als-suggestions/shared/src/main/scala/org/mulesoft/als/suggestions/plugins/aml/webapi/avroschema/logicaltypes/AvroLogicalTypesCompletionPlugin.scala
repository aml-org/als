package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.logicaltypes

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.{YMapEntry, YScalar}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AvroLogicalTypesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AvroLogicalTypesCompletionPlugin"

  // this methods manually checks the AST for the `logicalType` and it's value, as it is not represented in the AMF' model
  // improve this code when this information is available on the model
  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (
      params.astPartBranch.isKeyLike && !isInFieldValue(params) && params.astPartBranch.brothersKeys.contains(
        logicalType
      )
    ) Future {
      params.astPartBranch.brothers
        .collectFirst {
          case e: YMapEntry if e.key.value.toString.contains(logicalType) =>
            e.value.as[YScalar].text
        }
        .flatMap(facetMaps.get)
        .getOrElse(Seq.empty)
        .map(RawSuggestion(_, isAKey = true, category, mandatory = false))
    }
    else emptySuggestion

  private val category = "logical type"

  private val logicalType = "logicalType"

  private val facetMaps: Map[String, Seq[String]] = Map("decimal" -> Seq("precision", "scale"))
}
