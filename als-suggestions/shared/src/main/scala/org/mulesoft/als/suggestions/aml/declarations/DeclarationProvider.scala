package org.mulesoft.als.suggestions.aml.declarations

import amf.core.annotations.Aliases
import amf.core.model.document.{BaseUnit, DeclaresModel}
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.ElementNameExtractor._

import scala.collection.mutable

class DeclarationProvider(componentId: Option[String] = None) {

  case class DeclaredCandidates(local: Set[Name], alias: Set[Alias]) {
    def nonEmpty: Boolean = local.nonEmpty || alias.nonEmpty
  }

  def forNodeType(nodeTypeMapping: String): Set[Name] =
    declarations.getOrElse(nodeTypeMapping, Set.empty) ++ libraries
      .filter(t => t._2.isLocallyDeclared(nodeTypeMapping))
      .keys
      .toSet
  def forNodeType(nodeTypeMapping: String, alias: Alias): Set[Name] =
    libraries.get(alias).map(d => d.forNodeType(nodeTypeMapping)).getOrElse(Set.empty)

  type Alias           = String
  type NodeTypeMapping = String
  type Name            = String
  private val declarations: mutable.Map[NodeTypeMapping, Set[Name]] =
    mutable.Map.empty

  private val libraries: mutable.Map[Alias, DeclarationProvider] =
    mutable.Map.empty

  def put(typeMapping: NodeTypeMapping, elements: Set[Name]): Unit = declarations.get(typeMapping) match {
    case Some(set) => declarations.update(typeMapping, set ++ elements)
    case None      => declarations.put(typeMapping, elements)
  }

  def put(alias: Alias, provider: DeclarationProvider): Unit = libraries.get(alias) match {
    case Some(_) => libraries.update(alias, provider)
    case None    => libraries.put(alias, provider)
  }

  def isDeclared(nodeTypeMapping: NodeTypeMapping): Boolean =
    isLocallyDeclared(nodeTypeMapping) || libraries.exists(_._2.isLocallyDeclared(nodeTypeMapping))

  def isLocallyDeclared(nodeTypeMapping: NodeTypeMapping): Boolean = declarations.contains(nodeTypeMapping)
}

object DeclarationProvider {
  def apply(bu: BaseUnit, d: Option[Dialect]): DeclarationProvider = {
    val provider = new DeclarationProvider(d.flatMap(_.documents().declarationsPath().option()))
    bu match {
      case de: DeclaresModel => populateDeclares(de, provider)
      case _                 => // ignore
    }

    bu.annotations.find(classOf[Aliases]).foreach { a =>
      a.aliases.flatMap(l => bu.references.find(_.location().contains(l._2._1)).map(r => l._1 -> r)).foreach { t =>
        provider.put(t._1, DeclarationProvider(t._2, None))
      }
    }

    provider

  }

  private def populateDeclares(de: DeclaresModel, provider: DeclarationProvider): Unit =
    de.declares.foreach { d =>
      d.meta.`type`.foreach { iri =>
        provider.put(iri.iri(), d.elementIdentifier().toSet)
      }
    }

  private def elementIdentifier(element: DomainElement): Option[String] =
    element.fields
      .getValueAsOption(DialectDomainElementModel.DeclarationName)
      .map(_.value)
      .orElse(
        element.fields
          .fields()
          .find(fe => fe.field.value.iri() == (Namespace.Schema + "name").iri())
          .map(_.value.value))
      .collect({ case s: AmfScalar => s.value.toString })
}
