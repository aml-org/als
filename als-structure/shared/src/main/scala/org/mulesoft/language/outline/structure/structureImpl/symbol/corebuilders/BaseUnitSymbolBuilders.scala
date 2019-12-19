package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.Obj
import amf.core.model.document.{BaseUnit, DeclaresModel, EncodesModel}
import amf.core.model.domain.{AmfElement, DomainElement}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, ElementSymbolBuilder}

abstract class BaseUnitSymbolBuilder(element: BaseUnit)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[BaseUnit] {
  protected def nameFromMeta(obj: Obj): String

  private val encodedChildren = element match {
    case e: EncodesModel =>
      factory.builderFor(e.encodes).map(_.build()).getOrElse(Nil)
    case _ => Nil
  }

  private val declaredChildren: Map[String, Seq[ElementSymbolBuilder[_ <: AmfElement]]] = element match {
    case d: DeclaresModel =>
      val objToElements: Map[String, Seq[DomainElement]] =
        d.declares
          .filter(de => de.location() == element.location())
          .groupBy(d => nameFromMeta(d.meta))
      objToElements.map(t => (t._1, t._2.flatMap(e => factory.builderFor[DomainElement](e))))
    case _ => Map()
  }

  protected def buildDeclaredSymbols = {
    declaredChildren.flatMap {
      case (name, builders) =>
        val children = builders.flatMap(_.build()).sortWith((ds1, ds2) => ds1.range.start < ds2.range.start)

        children.toList match {
          case Nil => None
          case head :: tail =>
            Some(
              DocumentSymbol(name,
                             head.kind,
                             deprecated = false,
                             head.range + tail.lastOption.getOrElse(head).range,
                             head.selectionRange,
                             children.toList))
        }
    }
  }

  override def build(): Seq[DocumentSymbol] =
    encodedChildren ++ buildDeclaredSymbols
}
