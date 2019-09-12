package org.mulesoft.language.outline.structure.structureImpl

import amf.core.model.domain.{AmfElement, _}
import amf.core.parser.{FieldEntry, Value}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DomainElementSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.NameFieldSymbolBuilder

trait BuilderFactory {

  protected def companion: CompanionList =
    CompanionList(baseUnitBuilder) ++ List(DomainElementSymbolBuilder, NameFieldSymbolBuilder)
  private lazy val companionList: CompanionList = companion

  implicit val factory: BuilderFactory = this

  def baseUnitBuilder: ElementSymbolBuilderCompanion

  def builderFor[T <: AmfObject](obj: T): Option[ElementSymbolBuilder[_ <: AmfElement]] =
    builderFor[T](obj.meta.`type`.map(_.iri()), obj)

  def builderFor(e: FieldEntry, location: Option[String]): Option[ElementSymbolBuilder[_ <: AmfElement]] =
    e match {
      case FieldEntry(f, Value(v, _)) if location.forall(l => l == v.location().getOrElse(l)) =>
        builderFor(Seq(f.value.iri()), v)
          .orElse(builderForElement(v))
      case _ => None
    }

  def builderForElement(e: AmfElement): Option[ElementSymbolBuilder[_ <: AmfElement]] = {
    e match {
      case obj: AmfObject => builderFor(obj)
      case s: AmfScalar   => defaultScalarBuilder.map(f => f(s))
      case s: AmfArray    => defaultArrayBuilder.map(f => f(s))
    }
  }

  private def builderFor[T <: AmfElement](definitions: Seq[String],
                                          element: T): Option[ElementSymbolBuilder[_ <: AmfElement]] = {
    definitions match {
      case Nil => None
      case head :: tail =>
        val maybeOption: Option[ElementSymbolBuilder[_ <: AmfElement]] = companionList.map
          .get(head)
          .collect({ case c if c.isInstance(element) => c.construct(element.asInstanceOf[c.T]) })
          .flatten

        maybeOption.orElse(builderFor[T](tail, element))
    }
  }

  protected val defaultScalarBuilder: Option[AmfScalar => ElementSymbolBuilder[AmfScalar]] = None

  protected val defaultArrayBuilder: Option[AmfArray => ElementSymbolBuilder[AmfArray]] = None

}

class CompanionList(list: List[ElementSymbolBuilderCompanion]) {
  lazy val map: Map[String, ElementSymbolBuilderCompanion] = list.map(c => c.supportedIri -> c).toMap

  def +(builder: ElementSymbolBuilderCompanion) = new CompanionList(builder +: list)
  def ++(builders: List[ElementSymbolBuilderCompanion]) =
    new CompanionList(builders ++ list.filter(!builders.contains(_)))

  def -(builder: ElementSymbolBuilderCompanion) = new CompanionList(list.filter(_ != builder))
  def replaceFor(target: ElementSymbolBuilderCompanion, newValue: ElementSymbolBuilderCompanion) =
    new CompanionList(newValue +: list.filter(_ != target))
}

object CompanionList {
  def apply(element: ElementSymbolBuilderCompanion): CompanionList = new CompanionList(List(element))
}
