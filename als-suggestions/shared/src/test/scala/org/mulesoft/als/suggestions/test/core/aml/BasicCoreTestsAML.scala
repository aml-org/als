package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.CompletionsPluginHandler
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}
import org.mulesoft.high.level.InitOptions
import org.mulesoft.typesystem.definition.system.{OasCommonMediaTypes, OasResponseCodes}

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
      assert(result.length == 1 && result.forall(_.description == "dummy description"))
    }
  }
}
