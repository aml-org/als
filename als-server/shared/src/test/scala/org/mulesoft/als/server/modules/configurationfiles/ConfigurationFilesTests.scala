package org.mulesoft.als.server.modules.configurationfiles

import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.logger.EmptyLogger
import org.mulesoft.als.server.workspace.extract.WorkspaceRootHandler
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.scalatest.{AsyncFlatSpec, Matchers}
import org.mulesoft.als.configuration.ConfigurationStyle.{COMMAND, FILE}
import org.mulesoft.als.configuration.ProjectConfigurationStyle

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class ConfigurationFilesTests extends AsyncFlatSpec with Matchers with PlatformSecrets {

  override val executionContext: ExecutionContext = global

  behavior of "ProjectManager"
  private val okRoot = "file://als-server/shared/src/test/resources/configuration-files"
  it should "add a mainApi given a directory with exchange.json" in {
    for {
      instance <- AmfConfigurationWrapper()
      manager  <- Future(new WorkspaceRootHandler(instance, ProjectConfigurationStyle(FILE)))
      conf     <- manager.extractConfiguration(s"$okRoot/", EmptyLogger)
    } yield {
      conf.isDefined should be(true)
      conf.get.mainFile should be("api.raml")
    }
  }

  it should "Directory without exchange.json should not add any mainFile" in {

    for {
      instance <- AmfConfigurationWrapper()
      manager  <- Future(new WorkspaceRootHandler(instance, ProjectConfigurationStyle(FILE)))
      conf     <- manager.extractConfiguration(s"file://als-server/shared/src/test/resources/", EmptyLogger)
    } yield {
      conf.isEmpty should be(true)
    }
  }

  it should "Ignore mainApi given a directory with exchange.json but configured by command" in {
    for {
      instance <- AmfConfigurationWrapper()
      manager  <- Future(new WorkspaceRootHandler(instance, ProjectConfigurationStyle(COMMAND)))
      conf     <- manager.extractConfiguration(s"$okRoot/", EmptyLogger)
    } yield {
      conf.isEmpty should be(true)
    }
  }
}
