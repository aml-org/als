package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{AnnotationMapping, DialectDomainElement, SemanticExtension}
import amf.aml.internal.semantic.SemanticExtensionHelper
import amf.core.client.scala.model.document.Module
import amf.core.client.scala.model.domain.extensions.CustomDomainProperty
import amf.core.internal.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.internal.utils.QName
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.AnnotationReferenceCompletionPlugin.EXTENSION_CATEGORY
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.yaml.model.YMapEntry

import scala.concurrent.Future

object AnnotationReferenceCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AnnotationReferenceCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if (params.yPartBranch.isKey && !params.yPartBranch.isInArray && !params.amfObject
            .isInstanceOf[DialectDomainElement]) {
        val annSuggestions = AnnotationReferenceSuggester(params).suggest()
        if (isScalar(params) && annSuggestions.exists(_.category != EXTENSION_CATEGORY))
          RawSuggestion.forKey("value", "unknown", mandatory = false) +: annSuggestions
        else annSuggestions
      } else Nil
    )
  }

  private def isScalar(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.parentEntry match {
      case Some(e: YMapEntry) =>
        val entryName = e.key.asScalar.map(_.text)
        params.propertyMapping
          .find(_.name().option().exists(name => entryName.contains(name)))
          .exists(p => p.literalRange().option().isDefined && !p.allowMultiple().value())
      case _ => false
    }

  val EXTENSION_CATEGORY = "extensions"
}

case class AnnotationReferenceSuggester(params: AmlCompletionRequest) {

  private val modules: Map[String, Module] = params.baseUnit.aliasedModules

  val accompanied: Map[String, Dialect] =
    modules.flatMap(t => (t._2.references.collectFirst({ case d: Dialect => t._1 -> d })))

  private val qName: QName = QName(params.prefix.stripPrefix("("))

  val annName: Option[String] = params.branchStack.headOption match {
    case Some(c: CustomDomainProperty) => c.name.option()
    case _                             => None
  }

  private def isCompanionAlias(alias: String) = accompanied.contains(alias)

  private def isAccompanied(dialect: Dialect) = accompanied.values.exists(_.identifier == dialect.identifier)

  private def getAnnotationMapping(extension: SemanticExtension, d: Dialect): AnnotationMapping =
    SemanticExtensionHelper.findAnnotationMapping(d, extension)

  def appliesToDomain(mapping: AnnotationMapping) = {
    mapping.domain().flatMap(_.option()).exists(s => params.amfObject.meta.`type`.map(_.iri()).contains(s))
  }

  def rangeIsObject(mapping: AnnotationMapping) = mapping.objectRange().nonEmpty

  def isObject(extension: SemanticExtension, d: Dialect) = rangeIsObject(getAnnotationMapping(extension, d))

  private def searchExtension(name: String, filterFn: ((SemanticExtension, Dialect)) => Boolean) = {
    params.alsConfigurationState.findSemanticForName(name).filter(filterFn)
  }

  private def searchAndBuild(name: String,
                             filterFn: ((SemanticExtension, Dialect)) => Boolean,
                             insertText: String): Option[RawSuggestion] = {
    searchExtension(name, filterFn) match {
      case Some((extension, d)) => checkAndBuildExtensionSuggestion(extension, d, insertText)
      case _                    => Some(RawSuggestion.forKey(insertText, "annotations", mandatory = false))
    }
  }

  def buildAliasedSuggestion(name: String, aliasedDialect: Option[Dialect]): Option[RawSuggestion] = {
    searchAndBuild(name,
                   (t: (SemanticExtension, Dialect)) => aliasedDialect.exists(_.identifier == t._2.identifier),
                   s"(${qName.qualification}.$name)")
  }

  def buildSuggestion(name: String): Option[RawSuggestion] = {
    searchAndBuild(name, (t: (SemanticExtension, Dialect)) => !isAccompanied(t._2), s"($name)")
  }

  private def checkAndBuildExtensionSuggestion(extension: SemanticExtension,
                                               d: Dialect,
                                               insertText: String): Option[RawSuggestion] = {
    val mapping = getAnnotationMapping(extension, d)
    if (appliesToDomain(mapping)) Some(buildExtensionSuggestion(mapping, insertText))
    else None
  }

  private def buildExtensionSuggestion(mapping: AnnotationMapping, insertText: String): RawSuggestion = {
    if (rangeIsObject(mapping)) RawSuggestion.forObject(insertText, EXTENSION_CATEGORY)
    else RawSuggestion.forKey(insertText, EXTENSION_CATEGORY, mandatory = false)
  }

  def suggest(): Seq[RawSuggestion] = {
    if (qName.isQualified) {
      params.declarationProvider
        .forNodeType(CustomDomainPropertyModel.`type`.head.iri(), qName.qualification)
        .filterNot(annName.contains)
        .flatMap(an => buildAliasedSuggestion(an, accompanied.get(qName.qualification)))
        .toSeq
    } else
      params.declarationProvider
        .forNodeType(CustomDomainPropertyModel.`type`.head.iri())
        .filterNot(annName.contains)
        .flatMap(
          an =>
            if (an.contains("."))
              Some(
                RawSuggestion(s"($an",
                              isAKey = false,
                              if (isCompanionAlias(an.stripSuffix("."))) EXTENSION_CATEGORY else "annotations",
                              mandatory = false))
            else buildSuggestion(an)
        )
        .toSeq
  }

}
