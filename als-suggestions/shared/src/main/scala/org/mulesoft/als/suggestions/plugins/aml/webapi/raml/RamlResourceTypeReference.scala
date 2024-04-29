package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.EndPoint
import amf.apicontract.client.scala.model.domain.templates.ParametrizedResourceType
import amf.apicontract.internal.metamodel.domain.templates.ResourceTypeModel
import amf.apicontract.internal.spec.common.WebApiDeclarations.ErrorResourceType
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.model.domain.templates.ParametrizedDeclaration
import amf.core.internal.annotations.ErrorDeclaration
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}

object RamlResourceTypeReference extends RamlAbstractDeclarationReference {

  override def id: String = "RamlResourceTypeReferenceCompletionPlugin"

  override def entryKey: String = "type"

  override def iriDeclaration: String = ResourceTypeModel.`type`.head.iri()

  override protected def isValue(astPartBranch: ASTPartBranch): Boolean = astPartBranch.isValue

  override val elementClass: Class[_ <: DomainElement]                       = classOf[EndPoint]
  override val abstractDeclarationClass: Class[_ <: ParametrizedDeclaration] = classOf[ParametrizedResourceType]
  override val errorDeclarationClass: Class[_ <: ErrorDeclaration[_]]        = classOf[ErrorResourceType]
}
