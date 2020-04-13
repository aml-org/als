package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.metamodel.Obj
import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.DialectInstance
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.BaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.BaseUnitSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, ElementSymbolBuilder}

class AmlUnitSymbolBuilder(bu: BaseUnit)(override implicit val factory: BuilderFactory)
    extends BaseUnitSymbolBuilder(bu) {

  override protected def nameFromMeta(obj: Obj): String = {

    val dialect = bu match {
      case d: DialectInstance => AMLPlugin.registry.dialectFor(d)
      case _                  => None
    }
    dialect
      .flatMap(d =>
        d.documents().root().declaredNodes().find(p => p.id == obj.`type`.head.iri()).flatMap(_.name().option()))
      .getOrElse("unknown")
  }
}

object AmlUnitSymbolBuilder extends BaseUnitSymbolBuilderCompanion {
  override def construct(element: BaseUnit)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[BaseUnit]] =
    Some(new AmlUnitSymbolBuilder(element))
}
