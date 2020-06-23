package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}
import org.mulesoft.amfintegration.{AmfInstance, InitOptions}

import scala.concurrent.Future

class BasicCoreTestsAML extends CoreTest with DummyPlugins {

  def rootPath: String = "AML/demo"

  def format: String = Aml.toString

  test("full root structure") {
    runTestForCustomDialect("visit01.yaml", "dialect.yaml", Set("office", "date", "meetings"))
  }

  test("some root structure") {
    runTestForCustomDialect("visit02.yaml", "dialect.yaml", Set("date", "meetings"))
  }

  test("root structure with prefix") {
    runTestForCustomDialect("visit03.yaml", "dialect.yaml", Set("office"))
  }

  test("Custom Plugins completion Dummy") {
    val p             = filePath("dialect.yaml")
    val configuration = AmfInstance.default
    val s             = Suggestions.default
    for {
      _       <- configuration.init()
      dialect <- configuration.parse(p).map(_.baseUnit)
      result <- {
        val url = filePath("visit01.yaml")
        for {
          content <- platform.resolve(url)
          (env, position) <- Future.successful {
            val fileContentsStr = content.stream.toString
            val markerInfo      = this.findMarker(fileContentsStr)

            (this.buildEnvironment(url, markerInfo.patchedContent.original, content.mime), markerInfo.position)
          }
          suggestions <- {
            val suggestions = new Suggestions(platform, env, new PlatformDirectoryResolver(platform), configuration)
              .initialized()
            suggestions.completionsPluginHandler.cleanIndex()
            suggestions.completionsPluginHandler
              .registerPlugins(Seq(DummyCompletionPlugin(), DummyInvalidCompletionPlugin()), dialect.id)

            suggestions.suggest(url, position, snippetsSupport = true, None)
          }
        } yield suggestions
      }
    } yield {
      assert(result.length == 1 && result.forall(_.documentation.getOrElse("") == "dummy description"))
    }
  }
}
