package org.mulesoft.amfintegration.dialect.dialects.metadialect

object LibraryDocumentObjectNode extends DeclaresDocumentObjectNode {
  override def name: String = "LibraryDocumentObjectNode"

  override def nodeTypeMapping: String = "FakeToAvoidRootLibrary"
}
