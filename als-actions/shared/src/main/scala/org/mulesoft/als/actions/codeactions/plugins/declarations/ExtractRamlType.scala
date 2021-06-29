package org.mulesoft.als.actions.codeactions.plugins.declarations

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.BaseElementDeclarableExtractors
import org.mulesoft.als.common.ObjectInTree
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

trait ExtractRamlType extends BaseElementDeclarableExtractors {

  protected override lazy val amfObject: Option[AmfObject] = extractRamlType

  protected def extractRamlType: Option[AmfObject] =
    extractJsonSchema(maybeTree, params.dialect) orElse extractAmfObject(maybeTree, params.dialect)

  /**
    * Get the RAML type definition that encapsulates the Json schema, not the Json schema shape
    */
  protected override def extractable(maybeObject: Option[AmfObject], dialect: Dialect): Option[AmfObject] =
    super.extractable(maybeObject, dialect).filterNot(_.annotations.schemeIsJsonSchema)

  /**
    * Extract Json schema as a whole, do not allow to extract individual properties
    */
  private def extractJsonSchema(maybeTree: Option[ObjectInTree], dialect: Dialect): Option[AmfObject] =
    extractable(maybeTree.flatMap(_.stack.dropWhile(!_.annotations.schemeIsJsonSchema).drop(1).headOption), dialect)
}
