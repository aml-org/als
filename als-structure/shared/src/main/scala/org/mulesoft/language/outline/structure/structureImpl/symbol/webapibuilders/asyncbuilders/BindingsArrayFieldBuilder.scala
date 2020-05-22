package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.asyncbuilders

import amf.core.model.domain.AmfArray
import amf.core.parser
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.bindings.OperationBindingsModel
import org.mulesoft.amfmanager.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.DefaultWebApiArrayFieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

case class BindingsArrayFieldBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DefaultWebApiArrayFieldTypeSymbolBuilder(value, element) {

  override protected def range: Option[parser.Range] =
    super.range.orElse(value.values.headOption.flatMap(_.annotations.range()))

  override protected val children: List[DocumentSymbol] = Nil

  override protected def name: String = "bindings"
}

object BindingsArrayFieldCompanion extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(BindingsArrayFieldBuilder(value, element))

  override val supportedIri: String = OperationBindingsModel.Bindings.value.iri()
}
