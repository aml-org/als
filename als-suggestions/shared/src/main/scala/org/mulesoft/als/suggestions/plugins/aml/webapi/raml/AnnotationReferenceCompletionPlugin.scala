package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.scala.model.domain.extensions.CustomDomainProperty
import amf.core.internal.metamodel.domain.extensions.CustomDomainPropertyModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YMapEntry

import scala.concurrent.Future

object AnnotationReferenceCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AnnotationReferenceCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if (params.yPartBranch.isKey && !params.yPartBranch.isInArray) {
        val annName = params.branchStack.headOption match {
          case Some(c: CustomDomainProperty) => c.name.option()
          case _                             => None
        }
        val annSuggestions = params.declarationProvider
          .forNodeType(CustomDomainPropertyModel.`type`.head.iri())
          .filter(n => !annName.contains(n))
          .map(an => RawSuggestion.forKey(s"($an)", "annotations", mandatory = false))
          .toSeq
        if (isScalar(params)) RawSuggestion.forKey("value", "unknown", mandatory = false) +: annSuggestions
        else annSuggestions
      } else Nil
    )
  }

  private def isScalar(params: AmlCompletionRequest): Boolean = {

    params.yPartBranch.ancestorOf(classOf[YMapEntry]) match {
      case Some(e: YMapEntry) =>
        val entryName = e.key.asScalar.map(_.text)
        params.propertyMapping
          .find(_.name().option().exists(name => entryName.contains(name)))
          .exists(p => p.literalRange().option().isDefined && !p.allowMultiple().value())
      case _ => false
    }
  }

}
