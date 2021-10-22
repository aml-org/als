package org.mulesoft.amfintegration.dialect.dialects

import amf.aml.client.scala.model.domain.{DocumentsModel, PropertyMapping}
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ExternalFragmentDialect extends BaseDialect {
  override def DialectLocation: String = "file://vocabularies/dialects/externalfragment.yaml"

  override protected val name: String    = ""
  override protected val version: String = ""

  override protected def emptyDocument: DocumentsModel = DocumentsModel()

  override protected def encodes: DialectNode = new DialectNode {
    override def name: String = ""

    override def nodeTypeMapping: String = ""

    override def properties: Seq[PropertyMapping] = Nil
  }

  override val declares: Seq[DialectNode] = Nil

  override protected def declaredNodes: Map[String, DialectNode] = Map.empty
}
