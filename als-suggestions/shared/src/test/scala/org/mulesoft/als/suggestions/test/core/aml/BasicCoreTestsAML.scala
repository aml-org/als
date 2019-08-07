package org.mulesoft.als.suggestions.test.core.aml

import amf.core.remote.Aml
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

  test("Known Values - Response Codes") {
    runTestForCustomDialect("otherDialects/responseCodes.yaml",
                            "otherDialects/knownValuesDialect.yaml",
                            OasResponseCodes.all.toSet)
  }

  test("Known Values - Media Types") {
    runTestForCustomDialect("otherDialects/mediaTypes.yaml",
                            "otherDialects/knownValuesDialect.yaml",
                            OasCommonMediaTypes.all.toSet)
  }

  // BEGIN ARRAY
  test("AllowMultiple Enum - Single Value") {
    runTestForCustomDialect("otherDialects/testArraysInstance02.yaml",
                            "otherDialects/testArraysDialect.yaml",
                            Set("First", "Second", "Third", "Fourth"))
  }
  test("AllowMultiple Enum - Multiple Value") {
    runTestForCustomDialect("otherDialects/testArraysInstance01.yaml",
                            "otherDialects/testArraysDialect.yaml",
                            Set("Second", "Third", "Fourth"))
  }
  test("AllowMultiple Enum - Single Value w/prefix") {
    runTestForCustomDialect("otherDialects/testArraysInstance03.yaml",
                            "otherDialects/testArraysDialect.yaml",
                            Set("First", "Fourth"))
  }
  test("AllowMultiple Enum - Multiple Value w/prefix") {
    runTestForCustomDialect("otherDialects/testArraysInstance04.yaml",
                            "otherDialects/testArraysDialect.yaml",
                            Set("Fourth"))
  }
  // END ARRAY

  test("Custom Plugins completion Dummy") {

    for {
      dialect <- parseAMF(filePath("dialect.yaml"))
      _       <- Suggestions.init(InitOptions.AllProfiles)
      _ <- Future {
        CompletionsPluginHandler.cleanIndex()
        CompletionsPluginHandler
          .registerPlugins(Seq(DummyCompletionPlugin(), DummyInvalidCompletionPlugin()), dialect.id)
      }
      result <- suggest("visit01.yaml")
    } yield assert(result.length == 1 && result.forall(_.description == "dummy description"))
  }
}
