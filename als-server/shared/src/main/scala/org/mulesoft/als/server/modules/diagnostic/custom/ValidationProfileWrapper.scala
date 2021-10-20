package org.mulesoft.als.server.modules.diagnostic.custom

import amf.aml.client.scala.model.document.DialectInstance
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.vocabulary.Namespace
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp

trait ValidationElementWrapper[T <: DomainElement] {
  val element: T

  private val schemaNs: Namespace     = Namespace("http://schema.org/")
  private val shaclNs: Namespace      = Namespace("http://www.w3.org/ns/shacl#")
  private val validationNs: Namespace = Namespace("http://a.ml/vocabularies/amf-validation#")

  def getId: String = element.id

  protected def tryName(): Option[String] =
    element.graph
      .scalarByProperty("name".schemaOrg)
      .headOption
      .map(_.toString)

  implicit class NamespaceImpl(str: String) {
    def shacl: String = s"${shaclNs.base}$str"

    def schemaOrg: String = s"${schemaNs.base}$str"

    def validation: String = s"${validationNs.base}$str"
  }

}

case class ValidationProfileWrapper(instance: DialectInstance) extends ValidationElementWrapper[DomainElement] {
  val element: DomainElement = instance.encodes

  def name(): String =
    super.tryName().getOrElse(instance.identifier)

  def validations(): Seq[ValidationWrapper] = {
    element.graph
      .getObjectByProperty("validations".validation)
      .map(ValidationWrapper)
  }
}

case class ValidationWrapper(override val element: DomainElement) extends ValidationElementWrapper[DomainElement] {
  def name(): Option[String] =
    super.tryName()
}
