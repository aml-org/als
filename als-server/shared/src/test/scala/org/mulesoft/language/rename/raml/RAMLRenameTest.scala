package org.mulesoft.language.rename.raml

import org.mulesoft.language.rename.RenameTest

trait RAMLRenameTest extends RenameTest {

  def rootPath: String = "rename/raml"

  def format: String = "RAML 1.0"
}
