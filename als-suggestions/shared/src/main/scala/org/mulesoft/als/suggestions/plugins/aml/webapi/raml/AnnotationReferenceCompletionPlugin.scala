package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{AnnotationMapping, DialectDomainElement, SemanticExtension}
import amf.aml.internal.semantic.SemanticExtensionHelper
import amf.core.client.scala.model.document.Module
import amf.core.client.scala.model.domain.extensions.CustomDomainProperty
import amf.core.internal.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.internal.utils.QName
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.AnnotationReferenceCompletionPlugin.EXTENSION_CATEGORY
import org.mulesoft.als.suggestions.{AdditionalSuggestion, RawSuggestion}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.yaml.model.{YMapEntry, YNode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AnnotationReferenceCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AnnotationReferenceCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (
      params.yPartBranch.isKey && !params.yPartBranch.isInArray && !params.amfObject
        .isInstanceOf[DialectDomainElement]
    ) {
      AnnotationReferenceSuggester(params)
        .suggest()
        .map(annSuggestions =>
          if (isScalar(params) && annSuggestions.exists(_.category != EXTENSION_CATEGORY))
            RawSuggestion.forKey("value", "unknown", mandatory = false) +: annSuggestions
          else annSuggestions
        )
    } else Future.successful(Nil)

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
    modules.flatMap(t => t._2.references.collectFirst({ case d: Dialect => t._1 -> d }))

  private val qName: QName = QName(params.prefix.stripPrefix("("))

  val annName: Option[String] = params.branchStack.headOption match {
    case Some(c: CustomDomainProperty) => c.name.option()
    case _                             => None
  }

  private def isCompanionAlias(alias: String) =
    accompanied.contains(alias)

  private def isAccompanied(dialect: Dialect) =
    accompanied.values.exists(_.identifier == dialect.identifier)

  private def getAnnotationMapping(extension: SemanticExtension, d: Dialect): AnnotationMapping =
    SemanticExtensionHelper.findAnnotationMapping(d, extension)

  def appliesToDomain(mapping: AnnotationMapping): Boolean =
    mapping.domain().flatMap(_.option()).exists(s => params.amfObject.meta.`type`.map(_.iri()).contains(s))

  def rangeIsObject(mapping: AnnotationMapping): Boolean = mapping.objectRange().nonEmpty

  def isObject(extension: SemanticExtension, d: Dialect): Boolean = rangeIsObject(getAnnotationMapping(extension, d))

  private def searchExtension(
      name: String,
      filterFn: ((SemanticExtension, Dialect)) => Boolean
  ): Option[(SemanticExtension, Dialect)] =
    params.alsConfigurationState.findSemanticForName(name).filter(filterFn)

  private def searchAndBuild(
      name: String,
      filterFn: ((SemanticExtension, Dialect)) => Boolean,
      insertText: String
  ): Option[RawSuggestion] = {
    searchExtension(name, filterFn) match {
      case Some((extension, d)) => checkAndBuildExtensionSuggestion(extension, d, insertText)
      case _                    => Some(RawSuggestion.forKey(insertText, "annotations", mandatory = false))
    }
  }

  def buildAliasedSuggestion(name: String, aliasedDialect: Option[Dialect]): Option[RawSuggestion] = {
    searchAndBuild(
      name,
      (t: (SemanticExtension, Dialect)) => aliasedDialect.exists(_.identifier == t._2.identifier),
      s"(${qName.qualification}.$name)"
    )
  }

  def buildSuggestion(name: String): Option[RawSuggestion] =
    searchAndBuild(name, (t: (SemanticExtension, Dialect)) => !isAccompanied(t._2), s"($name)")

  private def checkAndBuildExtensionSuggestion(
      extension: SemanticExtension,
      d: Dialect,
      insertText: String
  ): Option[RawSuggestion] = {
    val mapping = getAnnotationMapping(extension, d)
    if (appliesToDomain(mapping)) Some(buildExtensionSuggestion(mapping, insertText))
    else None
  }

  private def buildExtensionSuggestion(mapping: AnnotationMapping, insertText: String): RawSuggestion = {
    if (rangeIsObject(mapping)) RawSuggestion.forObject(insertText, EXTENSION_CATEGORY)
    else RawSuggestion.forKey(insertText, EXTENSION_CATEGORY, mandatory = false)
  }

  def suggest(): Future[Seq[RawSuggestion]] =
    if (qName.isQualified)
      Future.successful(
        params.declarationProvider
          .forNodeType(CustomDomainPropertyModel.`type`.head.iri(), qName.qualification)
          .filterNot(annName.contains)
          .flatMap(an => buildAliasedSuggestion(an, accompanied.get(qName.qualification)))
          .toSeq
      )
    else
      notImportedCompanions.map(_ ++ localDeclared)

  private def suggestWithImport(semex: SemanticExtension, d: Dialect, companion: Module): RawSuggestion = {
    val libName          = AdditionalSuggestion.nameNotInList(d.name().value(), params.baseUnit.definedAliases)
    val relative: String = companion.identifier.relativize(params.baseUnit.identifier)
    val suggestion = RawSuggestion(
      s"($libName.${semex.extensionName().value()})",
      isAKey = true,
      EXTENSION_CATEGORY,
      mandatory = false
    )
    params.baseUnit.ast.fold(suggestion) { ast =>
      val defaultRange = AdditionalSuggestion
        .afterInfoNode(params.baseUnit, params.yPartBranch.isJson)
        .map(p => PositionRange(p, p))
        .getOrElse(PositionRange.TopLine)
      suggestion.withAdditionalTextEdits(
        Seq(Right(AdditionalSuggestion(YNode(relative), Seq("uses", libName), ast, defaultRange)))
      )
    }
  }

  private def notImportedCompanions: Future[Seq[RawSuggestion]] =
    for {
      seq <- Future.sequence(
        params.alsConfigurationState
          .findSemanticFor(params.amfObject.meta.`type`.headOption.map(_.iri()).getOrElse(""))
          .map(t =>
            params.alsConfigurationState.projectState
              .getCompanionForDialect(t._2)
              .map(maybeCompanion => (t._1, t._2, maybeCompanion))
          )
      )
    } yield {
      seq.flatMap {
        case (semex, dialect, Some(companion)) if !companionIsImported(companion) =>
          Some(suggestWithImport(semex, dialect, companion))
        case _ => None // already imported companions are taken care of
      }
    }

  private def companionIsImported(companion: Module): Boolean =
    params.baseUnit.references.map(_.identifier).contains(companion.identifier)

  private def localDeclared: Seq[RawSuggestion] =
    params.declarationProvider
      .forNodeType(CustomDomainPropertyModel.`type`.head.iri())
      .filterNot(annName.contains)
      .flatMap(an =>
        if (an.contains("."))
          Some(
            RawSuggestion(
              s"($an",
              isAKey = false,
              if (isCompanionAlias(an.stripSuffix("."))) EXTENSION_CATEGORY else "annotations",
              mandatory = false
            )
          )
        else buildSuggestion(an)
      )
      .toSeq
}
