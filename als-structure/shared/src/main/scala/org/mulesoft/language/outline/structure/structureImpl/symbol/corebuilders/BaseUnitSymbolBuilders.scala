package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.Obj
import amf.core.model.document.{BaseUnit, DeclaresModel, EncodesModel}
import amf.core.model.domain.{AmfElement, DomainElement}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, ElementSymbolBuilder}

abstract class BaseUnitSymbolBuilder(element: BaseUnit)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[BaseUnit] {
  protected def nameFromMeta(obj: Obj): String

  private val endodedChildren = element match {
    case e: EncodesModel =>
      factory.builderFor(e.encodes).map(_.build()).getOrElse(Nil)
    case _ => Nil
  }

  private val declaredChildrens: Map[String, Seq[ElementSymbolBuilder[_ <: AmfElement]]] = element match {
    case d: DeclaresModel =>
      val objToElements: Map[String, Seq[DomainElement]] =
        d.declares.groupBy(d => nameFromMeta(d.meta))
      objToElements.map(t => (t._1, t._2.flatMap(e => factory.builderFor[DomainElement](e))))
    case _ => Map()
  }

  private def buildDeclaredSymbols = {
    declaredChildrens.flatMap {
      case (name, builders) =>
        val childrens = builders.flatMap(_.build())

        childrens match {
          case Nil => None
          case head :: tail =>
            Some(
              DocumentSymbol(name,
                             head.kind,
                             false,
                             head.range + tail.lastOption.getOrElse(head).range,
                             head.selectionRange,
                             childrens.toList))
        }
    }
  }

  override def build(): Seq[DocumentSymbol] =
    endodedChildren ++ buildDeclaredSymbols
}
