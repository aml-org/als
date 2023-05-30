package org.mulesoft.language.outline.test

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.remote.Content
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.MarkerFinderTest
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.scalatest.compatible.Assertion
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.{ExecutionContext, Future}

trait OutlineTest[T] extends AsyncFunSuite with FileAssertionTest with PlatformSecrets with MarkerFinderTest {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def readDataFromAST(unit: BaseUnit, definedBy: Dialect): T

  def writeDataToString(data: T): String

  def emptyData(): T

  def runTest(
      path: String,
      jsonPath: String,
      configuration: Option[ALSConfigurationState] = None
  ): Future[Assertion] = {

    val fullFilePath = filePath(platform.encodeURI(path))
    val fullJsonPath = filePath(jsonPath)
    val futureAmfConfiguration: Future[ALSConfigurationState] =
      if (configuration.isDefined) Future(configuration.get)
      else
        EditorConfiguration().getState.map(state => ALSConfigurationState(state, EmptyProjectConfigurationState, None))
    for {
      amfConfiguration <- futureAmfConfiguration
      actualOutline    <- this.getActualOutline(fullFilePath, platform, amfConfiguration)
      tmp              <- writeTemporaryFile(jsonPath)(writeDataToString(actualOutline))
      r                <- assertDifferences(tmp, fullJsonPath)

    } yield r
  }

  def rootPath: String

  def buildLoaders(path: String, content: String): Seq[ResourceLoader] = {
    val loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == path

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
    })
    loaders
  }

  def getActualOutline(url: String, platform: Platform, configuration: ALSConfigurationState): Future[T] = {

    configuration
      .fetchContent(url)
      .flatMap(content => {
        // todo: check if this resource loader is necessary

        //        val fileContentsStr = content.stream.toString
        //        configuration.withResourceLoader(loader(url, fileContentsStr))
        configuration.parse(url).map(cu => (cu.result.baseUnit, cu.definedBy))
      })
      .map {
        case (amfUnit, d) =>
          readDataFromAST(amfUnit, d)
        case _ =>
          emptyData()
      } recoverWith {
      case e: Throwable =>
        println(e)
        Future.successful(emptyData())
      case _ => Future.successful(emptyData())
    }
  }

  private def loader(fileUrl: String, content: String) = new ResourceLoader {
    override def accepts(resource: String): Boolean = resource == fileUrl

    override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
  }

  def filePath(path: String): String = {
    s"file://als-structure/shared/src/test/resources/$rootPath/$path".replace('\\', '/').replace("null/", "")
  }

}
