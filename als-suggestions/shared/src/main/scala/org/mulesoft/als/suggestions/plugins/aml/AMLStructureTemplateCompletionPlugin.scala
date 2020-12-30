package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.{AmfArray, AmfScalar}
import amf.plugins.document.vocabularies.metamodel.domain.PropertyMappingModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, DialectNodeFinder}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLStructureTemplateCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLStructureTemplateCompletionPlugin"

  private def applies(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.isKey

  private val fullPrefix    = "New full"
  private val defaultPrefix = "New"
  private val category      = "template"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (applies(params))
      Future {
        val mappings = params.currentNode.map(_.propertiesMapping()).getOrElse(params.propertyMapping)
        parentTermKey(params).flatMap(_.mapTermKeyProperty().option()) match {
          case Some(key) =>
            val parent   = mappings.find(m => iriForMapping(m) == key)
            val children = mappings.filterNot(m => iriForMapping(m) == key).filter(_.minCount().value() > 0)
            (parent
              .map(
                _ =>
                  RawSuggestion
                    .forObject("",
                               category,
                               mandatory = true,
                               Some(s"$defaultPrefix element"),
                               children.flatMap(getFirstLevelTemplate(_, params))))
              .toSeq ++
              parent
                .map(
                  _ =>
                    RawSuggestion
                      .forObject("",
                                 category,
                                 mandatory = true,
                                 Some(s"$fullPrefix element"),
                                 children.flatMap(getFullTemplate(_, params))))
                .toSeq)
              .filter(_.children.nonEmpty)
          case None =>
            mappings.flatMap(pm => {
              (getFirstLevelTemplate(pm, params) ++ getFullTemplate(pm, params))
                .filter(rs => rs.children.nonEmpty) // if there is no child, then the snippet is not adding anything to the vanilla option
            })
        }
      } else emptySuggestion

  private def getFirstLevelTemplate(p: PropertyMapping, params: AmlCompletionRequest): Seq[RawSuggestion] =
    Seq(
      toRaw(p,
            defaultPrefix,
            requiredProperties(p, params)
              .map(c => RawAndIri(c, defaultPrefix))))

  private def getRecursiveChildren(p: PropertyMapping, params: AmlCompletionRequest): Seq[RawAndIri] =
    requiredProperties(p, params)
      .map(c => RawAndIri(c, fullPrefix, getRecursiveChildren(c, params)))

  private def getFullTemplate(p: PropertyMapping, params: AmlCompletionRequest): Seq[RawSuggestion] =
    Seq(
      toRaw(
        p,
        fullPrefix,
        getRecursiveChildren(p, params)
          .filter(_.rawSuggestion.children.nonEmpty) // if there is no nested child, it's same as first level suggestion
      )).filter(_.children.nonEmpty)

  private def requiredProperties(p: PropertyMapping, params: AmlCompletionRequest) =
    nestedPropertyMappings(p, params)
      .filter(_.minCount().value() > 0)

  private def nestedPropertyMappings(p: PropertyMapping, params: AmlCompletionRequest): Seq[PropertyMapping] = {
    val nodeType: Option[String] =
      p.fields
        .fields()
        .find(f => f.field.value.iri() == PropertyMappingModel.ObjectRange.value.iri())
        .map { _.value.value }
        .collect {
          case AmfArray(values, _) => values
        }
        .flatMap(_.collectFirst {
          case s: AmfScalar => s.toString
        })

    nodeType.map(mappingsForNode(_, params.actualDialect)).getOrElse(Seq.empty)
  }

  private def mappingsForNode(nodeType: String, d: Dialect): Seq[PropertyMapping] =
    d.declares
      .find(de => de.id == nodeType)
      .collect {
        case nm: NodeMapping =>
          nm.propertiesMapping()
      }
      .getOrElse(Seq.empty)

  private def toRaw(p: PropertyMapping, prefix: String, children: Seq[RawAndIri] = Seq.empty): RawSuggestion = {
    val name        = p.name().value()
    val displayText = Some(s"$prefix $name")

    if (p.mapTermKeyProperty().option().isDefined)
      namedSuggestion(p, children, prefix)
    else if (p.objectRange().nonEmpty || p.allowMultiple().value()) {
      if (p.allowMultiple().value() && p.mapTermKeyProperty().option().isEmpty)
        RawSuggestion.keyOfArray(name, category, displayText, children.map(_.rawSuggestion))
      else
        RawSuggestion.forObject(name, category, p.minCount().value() > 0, displayText, children.map(_.rawSuggestion))
    } else
      RawSuggestion.forKey(name,
                           category = category,
                           p.minCount().value() > 0,
                           displayText,
                           children.map(_.rawSuggestion))
  }

  private def namedSuggestion(p: PropertyMapping, children: Seq[RawAndIri], prefix: String) = {
    val name = p.name().value()
    val childrenSuggestion =
      children
        .filterNot(_.iri == p.mapTermKeyProperty().value())
        .map(_.rawSuggestion)
    val namelessChild =
      if (childrenSuggestion.nonEmpty) // no sense in suggesting an empty name for with no children
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

  private def parentTermKey(params: AmlCompletionRequest): Option[PropertyMapping] =
    params.branchStack.headOption
      .flatMap(DialectNodeFinder.find(_, None, params.actualDialect))
      .collectFirst({ case n: NodeMapping => n })
      .flatMap(_.propertiesMapping().find(_.mapTermKeyProperty().option().isDefined))

  case class RawAndIri(p: PropertyMapping, prefix: String, children: Seq[RawAndIri] = Seq.empty) {
    val rawSuggestion: RawSuggestion = toRaw(p, prefix, children)
    val iri: String =
      iriForMapping(p)
  }

  private def iriForMapping(p: PropertyMapping) =
    p.fields
      .fields()
      .find(f => f.field.value.iri() == PropertyMappingModel.NodePropertyMapping.value.iri())
      .map(_.value.value.toString)
      .getOrElse("")
}
