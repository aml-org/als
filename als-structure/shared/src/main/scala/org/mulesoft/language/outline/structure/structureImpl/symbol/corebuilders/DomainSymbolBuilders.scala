package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain._
import amf.core.parser.{Range => AmfRange}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.yaml.model.YMapEntry

/**
  * Builder for nodes that have structure(name, range, etc) and not should be skipped to show their sons
  * @tparam T
  */
trait StructuredSymbolBuilder[T <: AmfObject] extends AmfObjectSymbolBuilder[T] {

  protected val name: String
  protected val selectionRange: Option[PositionRange]

  protected def range: Option[PositionRange] =
    element.annotations
      .find(classOf[SourceAST])
      .flatMap(_.ast match {
        case yme: YMapEntry if yme.key.sourceName.isEmpty => None
        case yme: YMapEntry if yme.value.sourceName != yme.key.sourceName =>
          Some(PositionRange(yme.key.range))
        case y if y.sourceName.isEmpty => None
        case y                         => Some(PositionRange(y.range))
      })

  override def build(): Seq[DocumentSymbol] =
    if (name.isEmpty) Nil
    else
      range
        .map { r =>
          Seq(
            DocumentSymbol(name,
                           KindForResultMatcher.getKind(element),
                           deprecated = false,
                           r,
                           selectionRange.getOrElse(r),
                           skipLoneChild(children, name)))
        }
        .getOrElse(children)

  private def skipLoneChild(children: List[DocumentSymbol], name: String): List[DocumentSymbol] =
    if (children.length == 1 && children.head.name == name)
      children.head.children
    else
      children
}

class DomainElementSymbolBuilder(override val element: DomainElement, entryAst: YMapEntry)(
    override implicit val factory: BuilderFactory)
    extends StructuredSymbolBuilder[DomainElement] {

  val (name, selectionRange) =
    (entryAst.key.asScalar.map(_.text).getOrElse(entryAst.key.value.toString),
     Some(PositionRange(AmfRange(entryAst.key.range))))
}

object DomainElementSymbolBuilder extends AmfObjectSimpleBuilderCompanion[DomainElement] {
  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def getType: Class[_ <: AmfElement] = classOf[DomainElement]

  override def construct(element: DomainElement)(
      implicit factory: BuilderFactory): Option[SymbolBuilder[DomainElement]] =
    element match {
      case n: NamedDomainElement if n.name.option().isDefined =>
        NamedElementSymbolBuilder.construct(n).map(_.asInstanceOf[SymbolBuilder[DomainElement]])
      case _ =>
        element.annotations.find(classOf[SourceAST]).map(_.ast) match {
          case Some(entry: YMapEntry) =>
            Some(new DomainElementSymbolBuilder(element, entry))
          case _ => None
        }
    }
}

object NamedElementSymbolBuilder extends AmfObjectSimpleBuilderCompanion[NamedDomainElement] {

  override def getType: Class[_ <: AmfElement] = classOf[NamedDomainElement]

  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def construct(element: NamedDomainElement)(
      implicit factory: BuilderFactory): Option[NamedElementSymbolBuilder] =
    Some(new NamedElementSymbolBuilder(element)(factory))
}

trait NamedElementSymbolBuilderTrait[T <: NamedDomainElement] extends StructuredSymbolBuilder[T] {

  override protected val name: String = element.name.option().getOrElse("")
  override protected val selectionRange: Option[PositionRange] = element.name
    .annotations()
    .find(classOf[LexicalInformation])
    .map(l => PositionRange(l.range))
    .orElse(range)
}

class NamedElementSymbolBuilder(override val element: NamedDomainElement)(override val factory: BuilderFactory)
    extends NamedElementSymbolBuilderTrait[NamedDomainElement]
