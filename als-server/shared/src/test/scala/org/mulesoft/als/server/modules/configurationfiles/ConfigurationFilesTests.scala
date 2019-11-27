package org.mulesoft.als.server.modules.configurationfiles

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.workspace.extract.WorkspaceRootHandler
import org.scalatest.{FlatSpec, Matchers}

class ConfigurationFilesTests extends FlatSpec with Matchers with PlatformSecrets {

  behavior of "ProjectManager"
  private val okRoot = "file://als-server/shared/src/test/resources/configuration-files"

  it should "add a mainApi given a directory with exchange.json" in {
    val manager = new WorkspaceRootHandler(platform)
    val conf    = manager.extractMainFile(s"$okRoot/")
    conf.isDefined should be(true)
    conf.get.mainFile should be("api.raml")
  }

  it should "Directory without exchange.json should not add any mainFile" in {
    val manager = new WorkspaceRootHandler(platform)
    val conf    = manager.extractMainFile(s"file://als-server/shared/src/test/resources/")
    conf.isEmpty should be(true)
  }
}
