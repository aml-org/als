package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.core.model.domain._
import amf.core.parser.{FieldEntry, Range => AmfRange}
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.yaml.model.YMapEntry

trait FatherSymbolBuilder[T <: AmfObject] extends ElementSymbolBuilder[T] {
  val element: T

  def ignoreFields =
    List(WebApiModel.Name, DomainElementModel.Extends, LinkableElementModel.Target, WebApiModel.Version)

  def customBuilders: Seq[FieldEntry => CustomBuilder] =
    Seq(WebApiCustomArrayBuilder(_)(factory))

  def getCustomFromFieldEntry(fe: FieldEntry): Option[Seq[DocumentSymbol]] =
    customBuilders.find(c => c(fe).applies).map(_(fe)).map(_.build)

  protected def children: List[DocumentSymbol] =
    element.fields
      .fields()
      .filterNot(fe => ignoreFields.contains(fe.field))
      .toList
      .flatMap { getAllDocumentSymbols }

  private def getAllDocumentSymbols(fe: FieldEntry): Seq[DocumentSymbol] =
    getCustomFromFieldEntry(fe)
      .orElse {
        factory
          .builderFor(fe, element.location())
          .map(_.build())
      }
      .getOrElse(Nil)
}

trait AmfObjSymbolBuilder[T <: AmfObject] extends FatherSymbolBuilder[T] {

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
                           children))
        }
        .getOrElse(children)

}

class DomainElementSymbolBuilder(override val element: DomainElement, entryAst: YMapEntry)(
    override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[DomainElement] {

  val (name, selectionRange) =
    (entryAst.key.asScalar.map(_.text).getOrElse(entryAst.key.value.toString),
     Some(PositionRange(AmfRange(entryAst.key.range))))
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
          case _ => None
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
