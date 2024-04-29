package org.mulesoft.als.server.modules.completion.raml

import amf.core.client.common.remote.Content
import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.MockDiagnosticClientNotifier
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.scalatest.compatible.Assertion

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class IncludeCacheTests extends RAMLSuggestionTestServer {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("test01") {
    runTest(
      "includes/testGroup03/api1.raml",
      Set(
        "number",
        "any",
        "t",
        "date-only",
        "time-only",
        "datetime",
        "string",
        "datetime-only",
        "object",
        "nil",
        "array",
        "boolean",
        "file",
        "integer",
        "l."
      )
    )
  }

  def buildServer(rl: ResourceLoader): LanguageServer = {
    val factory = new WorkspaceManagerFactoryBuilder(
      new MockDiagnosticClientNotifier,
      EditorConfiguration.withPlatformLoaders(Seq(rl))
    )
      .buildWorkspaceManagerFactory()
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
      .addRequestModule(factory.completionManager)
      .build()
  }

  override def runTest(
      path: String,
      expectedSuggestions: Set[String],
      dialectPath: Option[String] = None
  ): Future[Assertion] = {
    val hitMap: mutable.Map[String, Int] = mutable.Map()
    val dummyResourceLoader: ResourceLoader = new ResourceLoader {
      override def fetch(resource: String): Future[Content] = Future.failed(new NotImplementedError("Not implemented"))

      override def accepts(resource: String): Boolean = {
        hitMap.get(resource) match {
          case Some(c) => hitMap.update(resource, c + 1)
          case _       => hitMap.put(resource, 1)
        }
        false
      }
    }
    val server = buildServer(dummyResourceLoader)

    val resolved = filePath(platform.encodeURI(path))
    for {
      _       <- server.testInitialize(AlsInitializeParams.default)
      content <- this.platform.fetchContent(resolved, AMFGraphConfiguration.predefined())
      (suggestions, map1) <- {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)
        hitMap.clear
        getServerCompletions(resolved, server, markerInfo).map { s =>
          val map = hitMap.toMap
          hitMap.clear
          (s, map)
        }
      }
      (suggestions2, map2) <- {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)
        hitMap.clear
        getServerCompletions(resolved, server, markerInfo).map { s =>
          val map = hitMap.toMap
          hitMap.clear
          (s, map)
        }
      }
    } yield {

      assert(suggestions == suggestions2) // cached and none cached suggestions should be the same

      assert((map2.toSet diff map1.toSet).isEmpty)

      // each resource should have been asked for only once (when AMF checks if the file exists)
      assert(map1.filterNot(_._1 == resolved).values.forall(_ == 1))
      assert(map2.filterNot(_._1 == resolved).values.forall(_ == 1))

      val resultSet = suggestions
        .map(item => item.textEdit.map(_.left.get.newText).orElse(item.insertText).value)
        .toSet
      val diff1 = resultSet.diff(expectedSuggestions)
      val diff2 = expectedSuggestions.diff(resultSet)

      if (diff1.isEmpty && diff2.isEmpty) succeed
      else
        fail(
          s"Difference for $path: got [${resultSet.mkString(", ")}] while expecting [${expectedSuggestions.mkString(", ")}]"
        )
    }
  }

}
