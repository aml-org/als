package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.metamodel.{Field, Obj}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfScalar
import amf.core.parser.Value
import amf.plugins.document.vocabularies.metamodel.document.DialectModel
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.BaseUnitSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, ElementSymbolBuilder}

case class AmlMetaDialectSymbolBuilder(di: Dialect)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(di) {
  override protected def nameFromMeta(obj: Obj): String = "NodeMappings"

  private val dialectNameBuilder = extractScalar(DialectModel.Version).map(AmlScalarSymbolBuilder("dialect", _))

  private val version = extractScalar(DialectModel.Version).map(AmlScalarSymbolBuilder("version", _))

  private def extractScalar(f: Field): Option[AmfScalar] =
    di.fields.getValueAsOption(f).collect({ case Value(s: AmfScalar, _) => s })

  private def documentsBuilder: Seq[DocumentSymbol] = factory.builderFor(di.documents()).map(_.build()).getOrElse(Nil)

  override protected def encodedChildren: Seq[DocumentSymbol] =
    (dialectNameBuilder ++ version).flatMap(_.build()).toSeq ++ documentsBuilder

}

object AmlMetaDialectSymbolBuilder extends BaseUnitSymbolBuilderCompanion {
  override def construct(element: BaseUnit)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[_ <: BaseUnit]] = {
    element match {
      case d: Dialect => Some(AmlMetaDialectSymbolBuilder(d))
      case _          => None
    }
  }
}
