package org.mulesoft.amfintegration.vocabularies.integration

import amf.aml.client.scala.model.document.Vocabulary
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.domain.common.{DescribedElementModel, NameFieldSchema}
import org.mulesoft.amfintegration.AMLRegistry
import org.mulesoft.amfintegration.AmfImplicits.DomainElementImp

import scala.collection.mutable

case class AlsVocabularyRegistry(initBases: Map[String, TermsDescription])
    extends AMLRegistry[Vocabulary]
    with VocabularyProvider {

  def getDescription(base: String, name: String): Option[String] =
    find(base).flatMap(_.find(name))

  def getDescription(valueType: ValueType): Option[String] =
    getDescription(valueType.iri().stripSuffix(valueType.name), valueType.name)

  def index(voc: Vocabulary): Unit =
    voc.base.option().foreach { base =>
      index(base, voc.declares)
    }

  private def find(base: String): Option[TermsDescription] = bases.get(base)

  private val bases: mutable.Map[String, TermsDescription] = {
    val mut: mutable.Map[String, TermsDescription] = mutable.Map()
    initBases.foreach(b => mut.put(b._1, b._2))
    mut
  }

  def index(base: String, terms: Seq[DomainElement]) = {
    bases.get(base) match {
      case Some(t) => terms.foreach(t.index)
      case None =>
        val description = TermsDescription()
        terms.foreach(description.index)
        bases.put(base, description)
    }
  }

  def branch: AlsVocabularyRegistry = new AlsVocabularyRegistry(bases.toMap)
}

sealed case class TermsDescription() {
  private val termsDescription: mutable.Map[String, String] = mutable.Map.empty

  def index(element: DomainElement): Unit =
    element.getLiteralProperty(NameFieldSchema.Name).map(_.toString).foreach(name => index(name, element))

  def index(name: String, element: DomainElement): Unit = {
    element
      .getLiteralProperty(DescribedElementModel.Description)
      .map(_.toString)
      .foreach(d => termsDescription.update(name, d))
  }
  def find(name: String): Option[String] = termsDescription.get(name)

  def allNames: Set[String] = termsDescription.keys.toSet
}

object AlsVocabularyRegistry {
  def apply(default: Seq[Vocabulary] = Seq.empty): AlsVocabularyRegistry = {
    val r = new AlsVocabularyRegistry(Map.empty)
    default.foreach(r.index)
    r
  }
}
