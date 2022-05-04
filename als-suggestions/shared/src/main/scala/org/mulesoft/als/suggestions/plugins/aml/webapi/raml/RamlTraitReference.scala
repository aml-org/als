package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.Operation
import amf.apicontract.client.scala.model.domain.templates.ParametrizedTrait
import amf.apicontract.internal.metamodel.domain.templates.TraitModel
import amf.apicontract.internal.spec.common.WebApiDeclarations.ErrorTrait
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.model.domain.templates.ParametrizedDeclaration
import amf.core.internal.annotations.ErrorDeclaration
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.yaml.model.YSequence

object RamlTraitReference extends RamlAbstractDeclarationReference {

  override def id: String = "RamlTraitReference"

  override protected def entryKey: String = "is"

  override protected def isArray(yPartBranch: YPartBranch): Boolean = !yPartBranch.isInArray

  override protected def iriDeclaration: String = TraitModel.`type`.head.iri()

  override protected def isValue(astPartBranch: ASTPartBranch): Boolean =
    astPartBranch.isValue || astPartBranch.parent.exists(_.isInstanceOf[YSequence])

  override protected val elementClass: Class[_ <: DomainElement]                       = classOf[Operation]
  override protected val abstractDeclarationClass: Class[_ <: ParametrizedDeclaration] = classOf[ParametrizedTrait]
  override protected val errorDeclarationClass: Class[_ <: ErrorDeclaration[_]]        = classOf[ErrorTrait]
}
