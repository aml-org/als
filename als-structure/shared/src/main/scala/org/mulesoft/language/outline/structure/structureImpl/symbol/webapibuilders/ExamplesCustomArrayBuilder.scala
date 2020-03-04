package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.model.domain.{AmfArray, NamedDomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.ParameterModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.FieldArrayBuilder
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol}

case class ExamplesCustomArrayBuilder(override implicit val factory: BuilderFactory) extends FieldArrayBuilder {
  override def applies(fe: FieldEntry): Boolean =
    fe.value.value.isInstanceOf[AmfArray] && ParameterModel.Examples == fe.field

  override protected def name(fe: FieldEntry): String =
    if (isSingleExample(fe)) "example" else "examples"

  private def isSingleExample(fe: FieldEntry) =
    fe.array.values match {
      case head +: Seq() =>
        head.asInstanceOf[NamedDomainElement].name.isNullOrEmpty
      case _ => false
    }
}
