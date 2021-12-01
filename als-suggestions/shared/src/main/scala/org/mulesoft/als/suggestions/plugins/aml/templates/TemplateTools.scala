package org.mulesoft.als.suggestions.plugins.aml.templates

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.aml.internal.metamodel.domain.PropertyMappingModel
import amf.core.client.scala.model.domain.{AmfArray, AmfObject, AmfScalar}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, DialectNodeFinder}
import org.mulesoft.amfintegration.AmfImplicits.{AmfObjectImp, DialectImplicits}
import org.yaml.model.YMapEntry
import amf.core.internal.utils.InflectorBase.Inflector

object TemplateTools {
  val fullPrefix          = "New full"
  val defaultPrefix       = "New"
  val category            = "template"
  private val defaultName = "element"

  private def maybeDeclarableName(amfObject: AmfObject, dialect: Dialect): Option[String] =
    amfObject.declarableKey(dialect)

  private def getName(params: AmlCompletionRequest) =
    maybeDeclarableName(params.amfObject, params.nodeDialect)
      .orElse(params.yPartBranch.parentEntry.flatMap(_.key.asScalar).map(_.text))
      .map(_.singularize)
      .getOrElse(defaultName)

  def fullTemplateSuggestionRaw(params: AmlCompletionRequest, children: Seq[RawSuggestion]): RawSuggestion =
    RawSuggestion.withNamedKey(children, TemplateTools.category, TemplateTools.fullPrefix, getName(params))

  def fullTemplateSuggestionPM(params: AmlCompletionRequest, children: Seq[PropertyMapping]): RawSuggestion =
    fullTemplateSuggestionRaw(params, children.flatMap(TemplateTools.getFullTemplate(_, params)))

  def firstTemplateSuggestionRaw(params: AmlCompletionRequest, children: Seq[RawSuggestion]): RawSuggestion =
    RawSuggestion.withNamedKey(children, TemplateTools.category, TemplateTools.defaultPrefix, getName(params))

  def firstTemplateSuggestionPM(params: AmlCompletionRequest, children: Seq[PropertyMapping]): RawSuggestion =
    firstTemplateSuggestionRaw(params, children.flatMap(TemplateTools.getFirstLevelTemplate(_, params)))

  def getFirstLevelTemplate(p: PropertyMapping, params: AmlCompletionRequest): Seq[RawSuggestion] =
    Seq(
      toRaw(p,
            defaultPrefix,
            requiredProperties(p, params)
              .map(c => RawAndIri(c, defaultPrefix))))

  def getFullTemplate(p: PropertyMapping, params: AmlCompletionRequest): Seq[RawSuggestion] =
    Seq(
      toRaw(
        p,
        fullPrefix,
        getRecursiveChildren(p, params)
          .filter(_.rawSuggestion.children.nonEmpty) // if there is no nested child, it's same as first level suggestion
      )).filter(_.children.nonEmpty)

  def parentTermKey(params: AmlCompletionRequest): Option[PropertyMapping] =
    params.branchStack.headOption
      .flatMap(DialectNodeFinder.find(_, None, params.actualDialect))
      .flatMap(_.propertiesMapping().find(_.mapTermKeyProperty().option().isDefined))
      .filterNot(
        pm =>
          params.amfObject.fields
            .fields()
            .map(_.field.value.iri())
            .exists(iri => pm.mapTermKeyProperty().option().contains(iri)))

  def iriForMapping(p: PropertyMapping): String =
    p.nodePropertyMapping().option().getOrElse("")

  def isInsideDeclaration(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.parentEntry
      .flatMap(_.key.asScalar)
      .map(_.text)
      .exists(k => params.nodeDialect.declarationsMapTerms.values.to[Seq].contains(k)) &&
      hasCorrectDeclarationNesting(params)

  private def getRecursiveChildren(p: PropertyMapping,
                                   params: AmlCompletionRequest,
                                   recursiveProperties: Seq[String] = Seq.empty): Seq[RawAndIri] = {
    if (recursiveProperties.contains(p.id)) Seq.empty
    else
      requiredProperties(p, params)
        .map(c => RawAndIri(c, fullPrefix, getRecursiveChildren(c, params, recursiveProperties :+ p.id)))
  }

  private def requiredProperties(p: PropertyMapping, params: AmlCompletionRequest) =
    nestedPropertyMappings(p, params)
      .filter(_.minCount().value() > 0)

  private def nestedPropertyMappings(p: PropertyMapping, params: AmlCompletionRequest): Seq[PropertyMapping] =
    p.fields
      .fields()
      .find(f => f.field.value.iri() == PropertyMappingModel.ObjectRange.value.iri())
      .map { _.value.value }
      .collect { case AmfArray(values, _) => values }
      .flatMap(_.collectFirst { case s: AmfScalar => s.toString })
      .map(mappingsForNode(_, params.actualDialect))
      .getOrElse(Seq.empty)

  private def mappingsForNode(nodeType: String, d: Dialect): Seq[PropertyMapping] =
    d.declares
      .find(de => de.id == nodeType)
      .collect { case nm: NodeMapping => nm.propertiesMapping() }
      .getOrElse(Seq.empty)

  private def toRaw(p: PropertyMapping, prefix: String, children: Seq[RawAndIri] = Seq.empty): RawSuggestion = {
    val name           = p.name().value()
    val displayText    = Some(s"$prefix $name")
    val sortedChildren = children.sortBy(r => r.iri) // apply an order to avoid flaky tests

    if (p.mapTermKeyProperty().option().isDefined)
      namedSuggestion(p, children, prefix)
    else if (p.objectRange().nonEmpty || p.allowMultiple().value()) {
      if (p.allowMultiple().value() && p.mapTermKeyProperty().option().isEmpty)
        RawSuggestion.keyOfArray(name, category, displayText, sortedChildren.map(_.rawSuggestion))
      else
        RawSuggestion.forObject(name,
                                category,
                                p.minCount().value() > 0,
                                displayText,
                                sortedChildren.map(_.rawSuggestion))
    } else
      RawSuggestion.forKey(name,
                           category = category,
                           p.minCount().value() > 0,
                           displayText,
                           sortedChildren.map(_.rawSuggestion))
  }

  private def namedSuggestion(p: PropertyMapping, children: Seq[RawAndIri], prefix: String) = {
    val name = p.name().value()
    val childrenSuggestion =
      children
        .filterNot(_.iri == p.mapTermKeyProperty().value())
        .map(_.rawSuggestion)
    val namelessChild =
      if (childrenSuggestion.nonEmpty) // no sense in suggesting an empty name with no children
        Seq(
          RawSuggestion
            .forObject("",
                       category,
                       mandatory = true,
                       None,
                       children
                         .filterNot(_.iri == p.mapTermKeyProperty().value())
                         .map(_.rawSuggestion)))
      else Seq.empty

    RawSuggestion
      .forObject(name, category, p.minCount().value() > 0, Some(s"$prefix $name"), namelessChild)
  }

  case class RawAndIri(p: PropertyMapping, prefix: String, children: Seq[RawAndIri] = Seq.empty) {
    val rawSuggestion: RawSuggestion = toRaw(p, prefix, children)
    val iri: String =
      iriForMapping(p)
  }

  private def hasCorrectDeclarationNesting(params: AmlCompletionRequest) =
    declarationPathForDialect(params.nodeDialect) match {
      case Some(dp) =>
        params.yPartBranch.stack.drop(3).collectFirst { case e: YMapEntry => e } match {
          case Some(entry) =>
            entry.key.asScalar.map(_.text).contains(dp)
          case _ => false
        }
      case _ =>
        params.yPartBranch.stack.size <= 4 // document -> map -> entry -> (node of previous declaration)
    }

  private def declarationPathForDialect(dialect: Dialect): Option[String] =
    dialect.documents().declarationsPath().option()
}
