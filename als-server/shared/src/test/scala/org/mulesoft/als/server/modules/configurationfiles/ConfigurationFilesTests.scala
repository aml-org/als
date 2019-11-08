package org.mulesoft.als.server.modules.configurationfiles

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.workspace.extract.WorkspaceRootHandler
import org.scalatest.{FlatSpec, Matchers}

class ConfigurationFilesTests extends FlatSpec with Matchers with PlatformSecrets {

  behavior of "ProjectManager"
  private val okRoot = "file://als-server/shared/src/test/resources/configuration-files"

  it should "add a mainApi given a directory with exchange.json" in {
    val manager = new WorkspaceRootHandler(platform)
    manager.addRootDir(s"$okRoot/")
    manager.getMainFiles should be(Set("api.raml"))
  }

  it should "Directory without exchange.json should not add any mainFile" in {
    val manager = new WorkspaceRootHandler(platform)
    manager.addRootDir(s"file://als-server/shared/src/test/resources/")
    manager.getMainFiles should be(Set.empty)
  }

  it should "add and remove the directory should result in no mainFile" in {
    val manager = new WorkspaceRootHandler(platform)
    manager.addRootDir(okRoot)
    manager.getMainFiles should be(Set("api.raml"))
    manager.removeRootDir(okRoot)
    manager.getMainFiles should be(Set.empty)
  }

  it should "provide the configFile for which the mainFile was taken" in {
    val manager = new WorkspaceRootHandler(platform)
    manager.addRootDir(okRoot)
    manager.getMainFiles should be(Set("api.raml"))
    manager.getUsedConfigFiles should be(Set(s"$okRoot/exchange.json"))
    manager.removeRootDir(okRoot)
    manager.getMainFiles should be(Set.empty)
    manager.getUsedConfigFiles should be(Set.empty)
  }
}
