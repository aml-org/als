package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.core.model.domain.{AmfElement, AmfObject, DomainElement, NamedDomainElement}
import amf.core.parser.{FieldEntry, Range => AmfRange}
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.lexer.InputRange
import org.yaml.model.YMapEntry

trait FatherSymbolBuilder[T <: AmfObject] extends ElementSymbolBuilder[T] {
  val element: T

  def ignoreFields =
    List(WebApiModel.Name, DomainElementModel.Extends, LinkableElementModel.Target, WebApiModel.Version)

  def customFieldFilters: Seq[FieldEntry => Boolean] = Nil

  private def finalFilters: Seq[FieldEntry => Boolean] =
    customFieldFilters :+ ((f: FieldEntry) => !ignoreFields.contains(f.field))
  protected def childrens: List[DocumentSymbol] =
    element.fields
      .fields()
      .filter(f => finalFilters.forall(fn => fn(f)))
      .flatMap(e => factory.builderFor(e))
      .flatMap(_.build())
      .toList
}

trait AmfObjSymbolBuilder[T <: AmfObject] extends FatherSymbolBuilder[T] {

  protected val name: String
  protected val selectionRange: Option[PositionRange]

  protected def range: Option[PositionRange] =
    element.annotations
      .find(classOf[LexicalInformation])
      .map(l => PositionRange(l.range))

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
                           childrens))
        }
        .getOrElse(childrens)

}

class ObjectElementSymbolBuilder(override val element: DomainElement)(override implicit val factory: BuilderFactory)
    extends FatherSymbolBuilder[DomainElement] {

  override def build(): Seq[DocumentSymbol] = childrens
}

class DomainElementSymbolBuilder(override val element: DomainElement, entryAst: YMapEntry)(
    override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[DomainElement] {

  val (name, selectionRange) =
    (entryAst.key.value.toString, Some(PositionRange(AmfRange(entryAst.key.range))))
}

object DomainElementSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = DomainElement
  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def getType: Class[_ <: AmfElement] = classOf[DomainElement]

  override def construct(element: DomainElement)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[_ <: DomainElement]] = {
    element match {
      case n: NamedDomainElement if n.name.option().isDefined =>
        NamedElementSymbolBuilder.construct(n)
      case _ =>
        element.annotations.find(classOf[SourceAST]).map(_.ast) match {
          case Some(entry: YMapEntry) =>
            Some(new DomainElementSymbolBuilder(element, entry))
          case _ => Some(new ObjectElementSymbolBuilder(element))
        }
    }
  }
}

object NamedElementSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = NamedDomainElement

  override def getType: Class[_ <: AmfElement] = classOf[NamedDomainElement]

  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def construct(element: NamedDomainElement)(
      implicit factory: BuilderFactory): Option[NamedElementSymbolBuilder] =
    Some(new NamedElementSymbolBuilder(element)(factory))
}

trait NamedElementSymbolBuilderTrait[T <: NamedDomainElement] extends AmfObjSymbolBuilder[T] {

  override protected val name: String = element.name.value()
  override protected val selectionRange: Option[PositionRange] = element.name
    .annotations()
    .find(classOf[LexicalInformation])
    .map(l => PositionRange(l.range))
    .orElse(range)
}

class NamedElementSymbolBuilder(override val element: NamedDomainElement)(override val factory: BuilderFactory)
    extends NamedElementSymbolBuilderTrait[NamedDomainElement]
