package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.suggestions.CompletionsPluginHandler
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}
import org.mulesoft.amfmanager.InitOptions

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
    val p = filePath("dialect.yaml")
    for {
      dialect <- parseAMF(p)
      _       <- Suggestions.init(InitOptions.AllProfiles)
      _ <- Future {
        CompletionsPluginHandler.cleanIndex()
        CompletionsPluginHandler
          .registerPlugins(Seq(DummyCompletionPlugin(), DummyInvalidCompletionPlugin()), dialect.id)
      }
      result <- suggest("visit01.yaml")
    } yield {
      AMLPlugin.registry.remove(p)
      println(result)
      assert(result.length == 1 && result.forall(_.documentation.getOrElse("") == "dummy description"))
    }
  }
}
