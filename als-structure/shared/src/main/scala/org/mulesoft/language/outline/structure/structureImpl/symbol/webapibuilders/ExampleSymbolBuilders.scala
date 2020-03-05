package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.{AmfElement, AmfScalar}
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.shapes.models.Example
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.AmfObjSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  DocumentSymbol,
  ElementSymbolBuilder,
  ElementSymbolBuilderCompanion
}

class ExampleSymbolBuilders(override val element: Example)(override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[Example] {
  override protected val name: String =
    element.name.option() match {
      case Some(n) => n
      case _ =>
        element.fields.fields().headOption match {
          case Some(f) if f.field == ExampleModel.MediaType => f.value.value.asInstanceOf[AmfScalar].toString
          case _                                            => "example"
        }
    }

  override protected def children: List[DocumentSymbol] = Nil

  override protected val selectionRange: Option[PositionRange] =
    element.annotations.find(classOf[LexicalInformation]).map(_.range).map(PositionRange.apply)
}

object ExampleSymbolBuilders extends ElementSymbolBuilderCompanion {
  override type T = Example

  override def getType: Class[_ <: AmfElement] = classOf[Example]

  override val supportedIri: String = ExampleModel.`type`.head.iri()

  override def construct(element: Example)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[Example]] =
    Some(new ExampleSymbolBuilders(element))
}
