package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.EndPoint
import amf.core.internal.metamodel.Field
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
class EndPointSymbolBuilder(override val element: EndPoint)(override implicit val ctx: StructureContext)
    extends ExtendsFatherSymbolBuilder[EndPoint] {

  override def ignoreFields: List[Field] = super.ignoreFields
  override protected val optionName: Option[String] =
    Some(element.path.value().stripPrefix(element.parent.flatMap(_.path.option()).getOrElse("")))
}
