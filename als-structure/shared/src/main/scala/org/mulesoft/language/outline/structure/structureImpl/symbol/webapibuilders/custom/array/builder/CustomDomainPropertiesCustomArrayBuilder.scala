package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.custom.array.builder

import amf.core.metamodel.Field
import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain.AmfArray
import amf.core.parser.FieldEntry
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.FieldArrayBuilder
import amf.core.parser.{Range => AmfRange}

case class CustomDomainPropertiesCustomArrayBuilder(override implicit val factory: BuilderFactory)
    extends FieldArrayBuilder {
  override def applies(fe: FieldEntry): Boolean =
    fe.value.value
      .isInstanceOf[AmfArray] && DomainElementModel.CustomDomainProperties == fe.field

  override protected def name(fe: FieldEntry): String = "Extensions"

  override protected def range(fe: FieldEntry): Option[PositionRange] =
    Some(PositionRange(AmfRange.NONE))
}
