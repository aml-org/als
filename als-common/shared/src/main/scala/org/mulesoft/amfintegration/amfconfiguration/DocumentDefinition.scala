package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{DocumentsModel, NodeMapping}
import amf.aml.internal.metamodel.document.DialectModel.Documents
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.shapes.client.scala.model.document.JsonSchemaDocument
import amf.shapes.internal.document.metamodel.JsonSchemaDocumentModel
import org.mulesoft.amfintegration.AmfImplicits.DialectImplicits

trait DocumentDefinition {
  // todo: check if all this is needed, I think most don't apply for other than Dialects
  def nameAndVersion(): String
  def name(): Option[String]
  def version(): Option[String]
  def documents(): Option[DocumentsModel]
  def declarationsMapTerms: Map[String, String]
  def findNodeMappingByTerm(term: String): Option[NodeMapping]
  def findNodeMapping(mappingId: String): Option[NodeMapping]
  def declares: Seq[DomainElement]
  def termsForId: Map[String, String]
  def isJsonStyle: Boolean
  val baseUnit: BaseUnit
}

sealed case class DialectDocumentDefinition(override val baseUnit: Dialect) extends DocumentDefinition {
  override def nameAndVersion(): String = baseUnit.nameAndVersion()

  override def name(): Option[String] = baseUnit.name().option()

  override def version(): Option[String] = baseUnit.version().option()

  override def documents(): Option[DocumentsModel] = Option(baseUnit.documents())

  override def declarationsMapTerms: Map[String, String] = baseUnit.declarationsMapTerms

  override def findNodeMappingByTerm(term: String): Option[NodeMapping] = baseUnit.findNodeMappingByTerm(term)

  override def findNodeMapping(mappingId: String): Option[NodeMapping] = baseUnit.findNodeMapping(mappingId)

  override def declares: Seq[DomainElement] = baseUnit.declares

  override def termsForId: Map[String, String] = baseUnit.termsForId

  override def isJsonStyle: Boolean = baseUnit.isJsonStyle
}

sealed case class JsonSchemaDocumentDefinition(override val baseUnit: JsonSchemaDocument) extends DocumentDefinition {
  override def nameAndVersion(): String = baseUnit.schemaVersion.option().getOrElse(baseUnit.id)

  override def name(): Option[String] = None

  override def version(): Option[String] = baseUnit.schemaVersion.option()

  override def documents(): Option[DocumentsModel] = None

  override def declarationsMapTerms: Map[String, String] = Map.empty

  override def findNodeMappingByTerm(term: String): Option[NodeMapping] = None

  override def findNodeMapping(mappingId: String): Option[NodeMapping] = None

  override def declares: Seq[DomainElement] = baseUnit.declares

  override def termsForId: Map[String, String] = Map.empty

  override def isJsonStyle: Boolean = true
}

object DocumentDefinition {
  def apply(document: Dialect): DocumentDefinition = DialectDocumentDefinition(document)
  def apply(document: JsonSchemaDocument): DocumentDefinition = JsonSchemaDocumentDefinition(document)
}