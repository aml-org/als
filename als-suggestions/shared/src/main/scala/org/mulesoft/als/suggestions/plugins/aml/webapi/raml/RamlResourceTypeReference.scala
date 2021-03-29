package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.DomainElement
import amf.core.model.domain.templates.ParametrizedDeclaration
import amf.core.annotations.ErrorDeclaration
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorResourceType
import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.models.EndPoint
import amf.plugins.domain.webapi.models.templates.ParametrizedResourceType
import org.mulesoft.als.common.YPartBranch

object RamlResourceTypeReference extends RamlAbstractDeclarationReference {

  override def id: String = "RamlResourceTypeReferenceCompletionPlugin"

  override def entryKey: String = "type"

  override def iriDeclaration: String = ResourceTypeModel.`type`.head.iri()

  override protected def isValue(yPartBranch: YPartBranch): Boolean = yPartBranch.isValue

  override val elementClass: Class[_ <: DomainElement]                       = classOf[EndPoint]
  override val abstractDeclarationClass: Class[_ <: ParametrizedDeclaration] = classOf[ParametrizedResourceType]
  override val errorDeclarationClass: Class[_ <: ErrorDeclaration[_]]        = classOf[ErrorResourceType]
}
