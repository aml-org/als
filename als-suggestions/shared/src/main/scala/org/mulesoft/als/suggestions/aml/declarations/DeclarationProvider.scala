package org.mulesoft.als.suggestions.aml.declarations

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.annotations.Aliases
import amf.core.internal.metamodel.domain.DomainElementModel
import org.mulesoft.als.common.SemanticNamedElement.ElementNameExtractor
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp

import scala.collection.mutable

class DeclarationProvider(componentId: Option[String] = None) {

  def findElement(newText: String, iriDeclaration: String): Option[DomainElement] =
    newText.split(".").toList match {
      case head :: tail if tail.nonEmpty => findLib(head, tail.head, iriDeclaration)
      case _ =>
        declarations.get(iriDeclaration).flatMap(d => d.find(_._1 == newText).map(_._2))
    }

  def findLib(libName: String, elementName: String, iriDeclaration: String): Option[DomainElement] = {
    libraries.get(libName).flatMap(l => l.findElement(elementName, iriDeclaration))
  }

  def isTermDeclarable(term: String): Boolean = declarableTerms.contains(term)

  case class DeclaredCandidates(local: Set[Name], alias: Set[Alias]) {
    def nonEmpty: Boolean = local.nonEmpty || alias.nonEmpty
  }

  def forNodeType(nodeTypeMapping: String): Set[Name] =
    declarations.getOrElse(nodeTypeMapping, Set.empty).map(_._1) ++
      getLocalAliases(nodeTypeMapping)

  def filterLocalByType(nodeTypeMapping: String): Set[DomainElement] =
    declarations.getOrElse(nodeTypeMapping, Set.empty).map(_._2)

  def forNodeType(nodeTypeMapping: String, alias: Alias): Set[Name] =
    libraries
      .get(alias)
      .map(d => d.forNodeType(nodeTypeMapping))
      .getOrElse(Set.empty)

  type Alias           = String
  type NodeTypeMapping = String
  type Name            = String
  private val declarations: mutable.Map[NodeTypeMapping, Set[(Name, DomainElement)]] =
    mutable.Map.empty

  private val libraries: mutable.Map[Alias, DeclarationProvider] =
    mutable.Map.empty

  private var declarableTerms: Seq[String] = Seq.empty

  def getElementByName(nodeTypeMapping: String): Set[_ <: (Name, DomainElement)] =
    declarations
      .getOrElse(nodeTypeMapping, Set.empty)

  def getLocalAliases(nodeTypeMapping: String): Set[Alias] =
    libraries
      .filter(t => t._2.isLocallyDeclared(nodeTypeMapping))
      .keys
      .map(_ + ".")
      .toSet

  def getElementByName(nodeTypeMapping: String, alias: String): Set[_ <: (Name, DomainElement)] =
    libraries
      .get(alias)
      .map(d => d.getElementByName(nodeTypeMapping))
      .getOrElse(Set.empty)

  def putDeclarable(str: String): Unit =
    declarableTerms = str +: declarableTerms

  def put(typeMapping: NodeTypeMapping, element: DomainElement): Unit =
    declarations.get(typeMapping) match {
      case Some(set) =>
        declarations.update(typeMapping, set ++ element.elementIdentifier().map(n => (n, element)).toSet)
      case None => declarations.put(typeMapping, element.elementIdentifier().map(n => (n, element)).toSet)
    }

  def put(alias: Alias, provider: DeclarationProvider): Unit =
    libraries.get(alias) match {
      case Some(_) => libraries.update(alias, provider)
      case None    => libraries.put(alias, provider)
    }

  def isLocallyDeclared(nodeTypeMapping: NodeTypeMapping): Boolean =
    declarations.contains(nodeTypeMapping)

  def filterLocal(element: Name, mapping: NodeTypeMapping): this.type = {
    declarations.get(mapping) match {
      case Some(set) =>
        declarations.update(mapping, set.filterNot(_._2.elementIdentifier().getOrElse("") == element))
      case None => // ignore
    }
    this
  }
}

object DeclarationProvider {
  def apply(bu: BaseUnit, d: Option[Dialect]): DeclarationProvider = {
    val provider = new DeclarationProvider(d.flatMap(_.documents().declarationsPath().option()))

    populateDeclarables(d, provider)

    bu match {
      case de: DeclaresModel => populateDeclares(de, provider)
      case _                 => // ignore
    }

    bu.annotations.find(classOf[Aliases]).foreach { a =>
      a.aliases
        .flatMap(l =>
          bu.references
            .find(_.location().contains(l._2.fullUrl))
            .map(r => l._1 -> r)
        )
        .foreach { t =>
          provider.put(t._1, DeclarationProvider(t._2, None))
        }
    }

    provider

  }

  private def populateDeclarables(d: Option[Dialect], provider: DeclarationProvider): Unit = {
    val declaredIds = d
      .map(_.documents())
      .map(documents => {
        val libDec: Seq[String] = Option(documents.library())
          .map(_.declaredNodes().flatMap(_.fields.fields()).map(_.value.toString))
          .getOrElse(Seq())
        val fragEnc: Seq[String] =
          Option(documents.fragments())
            .map(_.flatMap(_.fields.fields().map(_.value.toString)))
            .getOrElse(Seq())
        val rootDec: Seq[String] = Option(documents.root())
          .map(_.declaredNodes().flatMap(_.mappedNode().option()))
          .getOrElse(Seq())

        libDec ++ fragEnc ++ rootDec
      })
      .getOrElse(Seq())

    val nodes =
      d.map(_.declares.collect({ case n: NodeMapping => n })).getOrElse(Nil)
    declaredIds.foreach { id =>
      nodes.find(_.id == id).foreach { nm =>
        provider.putDeclarable(nm.nodetypeMapping.value())
      }
    }

  }

  private def populateDeclares(de: DeclaresModel, provider: DeclarationProvider): Unit =
    de.declares.foreach { d =>
      d.metaURIs
        .filter(_ != DomainElementModel.`type`.head.iri())
        .foreach { iri =>
          provider.put(iri, d)
        }
    }
}
