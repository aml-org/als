package org.mulesoft.amfintegration

import amf.core.Root
import amf.core.client.ParsingOptions
import amf.core.metamodel.domain.common.{DescriptionField, NameFieldSchema}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.DomainElement
import amf.core.parser.ParserContext
import amf.core.remote._
import amf.core.vocabulary.ValueType
import amf.internal.environment.Environment
import amf.internal.resource.StringResourceLoader
import amf.plugins.document.vocabularies.emitters.common.IdCounter
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstanceUnit, Vocabulary}
import amf.plugins.document.vocabularies.{AMLPlugin, DialectsRegistry}
import org.mulesoft.amfintegration.AmfImplicits.DomainElementImp
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.collection.mutable
import scala.concurrent.Future

class AlsDialectsRegistry extends DialectsRegistry {

  private val alsDialects: mutable.Map[Vendor, Dialect] = mutable.Map.empty

  def addWebApiDialect(d: Dialect, vendor: Vendor): Unit = alsDialects.update(vendor, d)

  def amlAdnWebApiDialects: Iterable[Dialect] = alsDialects.values ++ allDialects()

  private def dialectForVendor(bu: BaseUnit): Option[Dialect] = bu.sourceVendor.flatMap(v => alsDialects.get(v))

  def dialectForUnit(bu: BaseUnit): Option[Dialect] = bu match {
    case _: Dialect               => Some(MetaDialect.dialect)
    case _: Vocabulary            => Some(VocabularyDialect.dialect)
    case diu: DialectInstanceUnit => dialectFor(diu)
    case _                        => dialectForVendor(bu)
  }

  def dialectForVendor(v: Vendor): Option[Dialect] = alsDialects.get(v)

  def registerDialect(content: String): Future[Dialect] = {
    val loader = StringResourceLoader(nextDialectUri(), content)
    registerDialect(loader.url, Environment().add(loader))
  }

  private val counter = new IdCounter()

  private def nextUri = s"file://${counter.genId("temp-dialect")}.yaml"

  private def nextDialectUri() = {
    var next = nextUri
    while (map.contains(next)) next = nextUri
    next
  }
}

case class AlsVocabularyRegistry() {
  case class TermsDescription() {
    private val termsDescription: mutable.Map[String, String] = mutable.Map.empty
    def index(element: DomainElement): Unit =
      element.getLiteralProperty(NameFieldSchema.Name).map(_.toString).foreach(name => index(name, element))

    def index(name: String, element: DomainElement): Unit = {
      element
        .getLiteralProperty(DescriptionField.Description)
        .map(_.toString)
        .foreach(d => termsDescription.update(name, d))
    }

    def find(name: String): Option[String] = termsDescription.get(name)

  }

  def find(base: String): Option[TermsDescription] = bases.get(base)

  def getDescription(base: String, name: String): Option[String] =
    find(base).flatMap(_.find(name))

  private val bases: mutable.Map[String, TermsDescription] = mutable.Map.empty

  private def index(base: String, terms: Seq[DomainElement]) = {
    bases.get(base) match {
      case Some(t) => terms.foreach(t.index)
      case None =>
        val description = TermsDescription()
        terms.foreach(description.index)
        bases.put(base, description)

    }
  }

  def index(voc: Vocabulary): Unit = {
    voc.base.option().foreach { base =>
      index(base, voc.declares)
    }
  }
}

trait SemanticDescriptionProvider {
  def getSemanticDescription(v: ValueType): Option[String]
}

case class ALSAMLPlugin() extends AMLPlugin with SemanticDescriptionProvider {

  override val registry = new AlsDialectsRegistry()

  override def getSemanticDescription(v: ValueType): Option[String] =
    vocabularyRegistry.getDescription(v.ns.base, v.name)

  def registerWebApiDialect(vendor: Vendor, d: Dialect): Unit = registry.addWebApiDialect(d, vendor)

  def registerWebApiDialect(vendor: Vendor): Unit = vendor match {
    case Raml =>
      registerWebApiDialect(Raml10, Raml10TypesDialect())
      registerWebApiDialect(Raml08, Raml08TypesDialect())
    case Raml10 => registerWebApiDialect(Raml10, Raml10TypesDialect())
    case Raml08 => registerWebApiDialect(Raml08, Raml08TypesDialect())
    case Oas =>
      registerWebApiDialect(Oas20, OAS20Dialect())
      registerWebApiDialect(Oas30, OAS30Dialect())
    case Oas30 =>
      registerWebApiDialect(Oas30, OAS30Dialect())
    case Oas20 =>
      registerWebApiDialect(Oas20, OAS20Dialect())
    case AsyncApi20 =>
      registerWebApiDialect(AsyncApi20, AsyncApi20Dialect())
    case _ => // ignore
  }
  val vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry()

  def dialectFor(u: BaseUnit): Option[Dialect] = registry.dialectForUnit(u)
  def dialectFor(v: Vendor): Option[Dialect]   = registry.dialectForVendor(v)

  override def parse(document: Root, parentContext: ParserContext, options: ParsingOptions): Option[BaseUnit] = {
    super.parse(document, parentContext, options).map {
      case v: Vocabulary =>
        vocabularyRegistry.index(v)
        v
      case other => other
    }
  }

}
