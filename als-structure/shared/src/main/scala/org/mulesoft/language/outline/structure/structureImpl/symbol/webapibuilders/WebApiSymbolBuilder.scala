package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.metamodel.document.BaseUnitModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import amf.plugins.domain.webapi.models.EndPoint
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.RamlEndPointSymbolBuilder

trait BaseUnitSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[BaseUnit] {

  override def getType: Class[_ <: AmfElement] = classOf[BaseUnit]
  override val supportedIri: String            = BaseUnitModel.`type`.head.iri()
}

class LicenseFieldSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ObjectFieldTypeSymbolBuilder {
  override protected val name: Option[String] = Some("License")
}

object LicenseFieldSymbolBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {

  override val supportedIri: String = WebApiModel.License.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new LicenseFieldSymbolBuilder(value, element))
}

object EndPointFieldBuilderCompanion extends IriFieldSymbolBuilderCompanion with ArrayFieldTypeSymbolBuilderCompanion {

  override val supportedIri: String = WebApiModel.EndPoints.value.iri()

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new EndPointFieldSymbolBuilder(value, element))
}

class EndPointFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {

  override def build(): Seq[DocumentSymbol] = {

    val endpoints = value.values.collect({ case e: EndPoint => e })
    endpoints
      .collect({
        case e: EndPoint if e.parent.isEmpty =>
          RamlEndPointSymbolBuilder(e, endpoints)(factory)
      })
      .flatMap(_.build())
  }

  override protected val name: Option[String] = None
}
