package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.Field
import amf.core.parser.Range
import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
class EndPointSymbolBuilder(override val element: EndPoint)(override implicit val ctx: StructureContext)
    extends ExtendsFatherSymbolBuilder[EndPoint] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    Some(element.path.value().stripPrefix(element.parent.flatMap(_.path.option()).getOrElse("")))

  override protected val selectionRange: Option[Range] =
    element.path.annotations().find(classOf[LexicalInformation]).map(l => l.range)
}
