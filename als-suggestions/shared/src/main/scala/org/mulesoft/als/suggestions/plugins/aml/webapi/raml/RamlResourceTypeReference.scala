package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.models.EndPoint
import amf.plugins.domain.webapi.models.templates.ParametrizedResourceType
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLDeclarationsReferencesCompletionPlugin
import org.mulesoft.als.suggestions.{CompletionParams, RawSuggestion}
import org.yaml.model.{YMapEntry, YNode}

import scala.concurrent.Future

object RamlResourceTypeReference extends CompletionPlugin {
  override def id: String = "RamlResourceTypeReferenceCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if ((params.amfObject.isInstanceOf[EndPoint]
          || params.amfObject.isInstanceOf[ParametrizedResourceType])
          && isResourceTypeDef(params.yPartBranch)) {
        new AMLDeclarationsReferencesCompletionPlugin(
          Seq(ResourceTypeModel.`type`.head.iri()),
          stringValue(params.yPartBranch),
          params.declarationProvider,
          None).resolve().map(r => r.copy(isKey = params.yPartBranch.isKey))
      } else Nil)

  }

  private def isResourceTypeDef(yPartBranch: YPartBranch) =
    isValueInType(yPartBranch) || isKeyInTypeMap(yPartBranch)

  /**
    * /endpoint:
    *   type: * || type: res*
    *case type 1 is object enpoint other cases are parametrized declaration parser
    */
  private def isValueInType(yPartBranch: YPartBranch) =
    yPartBranch.isValue && yPartBranch.parentEntryIs("type")

  /**
    * /endpoint:
    *   type:
    *     res*
    */
  private def isKeyInTypeMap(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKey && yPartBranch
      .ancestorOf(classOf[YMapEntry])
      .exists(_.key.asScalar.exists(_.text == "type"))

  private def stringValue(yPart: YPartBranch) = {
    yPart.node match {
      case n: YNode => n.asScalar.map(_.text).getOrElse("")
      case _        => ""
    }
  }
}
