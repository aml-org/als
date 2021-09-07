package org.mulesoft.als.server.modules.configurationfiles

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.workspace.extract.WorkspaceRootHandler
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.scalatest.{AsyncFlatSpec, Matchers}
import org.mulesoft.als.configuration.ConfigurationStyle.{COMMAND, FILE}
import org.mulesoft.als.configuration.ProjectConfigurationStyle

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class ConfigurationFilesTests extends AsyncFlatSpec with Matchers with PlatformSecrets {

  override val executionContext: ExecutionContext = global

  behavior of "ProjectManager"
  private val okRoot = "file://als-server/shared/src/test/resources/configuration-files"

  it should "add a mainApi given a directory with exchange.json" in {
    val manager = new WorkspaceRootHandler(AmfConfigurationWrapper(), ProjectConfigurationStyle(FILE))
    manager.extractConfiguration(s"$okRoot/", EmptyLogger).map { conf =>
      conf.isDefined should be(true)
      conf.get.mainFile should be("api.raml")
    }
  }

  it should "Directory without exchange.json should not add any mainFile" in {
    val manager = new WorkspaceRootHandler(AmfConfigurationWrapper(), ProjectConfigurationStyle(FILE))
    manager.extractConfiguration(s"file://als-server/shared/src/test/resources/", EmptyLogger).map {
      _.isEmpty should be(true)
    }
  }

  it should "Ignore mainApi given a directory with exchange.json but configured by command" in {
    val manager = new WorkspaceRootHandler(AmfConfigurationWrapper(), ProjectConfigurationStyle(COMMAND))
    manager.extractConfiguration(s"$okRoot/", EmptyLogger).map {
      _.isEmpty should be(true)
    }
  }
}
