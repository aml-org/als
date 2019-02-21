package org.mulesoft.language.outline

import amf.client.remote.Content
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.language.common.dtoTypes.OpenedDocument
import org.mulesoft.language.outline.test.OutlineTest
import org.mulesoft.language.test.LanguageServerTest

import scala.concurrent.Future

object File {
  val FILE_PROTOCOL = "file://"

  def unapply(url: String): Option[String] = {
    url match {
      case s if s.startsWith(FILE_PROTOCOL) =>
        val path = s.stripPrefix(FILE_PROTOCOL)
        Some(path)
      case _ => None
    }
  }
}

trait AlsOutlineTest[T] extends LanguageServerTest {

//  def runTest(path: String, jsonPath: String): Future[Assertion] = {
//
//    val fullFilePath = filePath(path)
//    val fullJsonPath = filePath(jsonPath)
//
//    for {
//      _ <- org.mulesoft.high.level.Core.init()
//      actualOutline <- this.getActualOutline(fullFilePath, path)
//      expectedOutlineStr <- this.getExpectedOutline(fullJsonPath)
//    } yield {
//      val expectedOutline: TransportType = readDataFromString(expectedOutlineStr)
//      val diffs = compare(actualOutline, expectedOutline, "actual", "expected")
//      if (diffs.isEmpty) {
//        succeed
//      } else {
//        // var actualJSON = serialize(actualOutline)
//        // platform.write(fullJsonPath,actualJSON)
//        val message = diffs.mkString("\n")
//        fail(message)
//      }
//    }
//  }

}

class MarkerInfo(val content: String, val position: Int) {}
