package org.mulesoft.als.server.modules.rename.jsonSchema

import org.mulesoft.als.server.modules.rename.ServerRenameTest

trait JsonSchemaRenameTest extends ServerRenameTest {
  def rootPath: String = "rename/jsonSchema/test.001/draft-03.propertyName.json"
}
