package org.mulesoft.language.outline.structure.structureImpl

import amf.core.model.domain._
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.SymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._

trait BuilderFactory {

  // separate to static in object for avoid recalculate all the list in AML factory instances
  protected def companion: FieldCompanionList =
    FieldCompanionList(
      List(
        DeclaresFieldSymbolBuilderCompanion,
        EncodesFieldSymbolBuilderCompanion,
        DefaultArrayFieldTypeSymbolBuilderCompanion,
        ReferencesFieldSymbolBuilderCompanion,
        VariableFieldSymbolBuilderCompanion
      ),
      List(BaseUnitSymbolBuilderCompanion, DomainElementSymbolBuilder)
    )

  private lazy val companionList: FieldCompanionList = companion

  def builderFor(obj: AmfObject)(implicit ctx: StructureContext): Option[SymbolBuilder[_ <: AmfObject]] = {
    if (obj.location().forall(l => l == ctx.location)) {
      val option = companionList.find(obj)
      //      debugBuilders(option)
      option
    } else None
  }

  def builderFor(e: FieldEntry)(implicit ctx: StructureContext): Option[SymbolBuilder[FieldEntry]] = {
    if (e.value.value.location().forall(l => l == ctx.location)) {
      val option = companionList.find(e)
      //      debugBuilders(option)
      option
    } else None
  }

//  private def debugBuilders(option: Option[SymbolBuilder[_]]): Unit =
//    option.foreach{b =>
//      println(s"${b.getClass.getName}")
//      println(s"\t${b.build().map(_.name).mkString("\n\t")}")
//
//    }
}
