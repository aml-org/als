package org.mulesoft.als.suggestions.test.core.aml

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.remote.Spec
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  EditorConfiguration,
  EmptyProjectConfigurationState
}

import scala.concurrent.Future

class BasicCoreTestsAML extends CoreTest with DummyPlugins {

  def rootPath: String = "AML/demo"

  def format: String = Spec.AML.toString

  test("full root structure") {
    runTestForCustomDialect("visit01.yaml", "dialect.yaml", Set("office", "date", "meetings", "New meetings"))
  }

  test("some root structure") {
    runTestForCustomDialect("visit02.yaml", "dialect.yaml", Set("date", "meetings", "New meetings"))
  }

  test("root structure with prefix") {
    runTestForCustomDialect("visit03.yaml", "dialect.yaml", Set("office"))
  }

  test("enum values") {
    runTestForCustomDialect("visit04.yaml", "dialect.yaml", Set("Pilar", "BA", "SFO", "Chicago", "Palo Alto"))
  }

  // todo: when fixing ALS-1805 check if this works. If not then create corresponding ticket
  ignore("key new meeting") {
    runTestForCustomDialect("visit05.yaml", "dialect.yaml", Set("New meeting"))
  }

  // todo: fix in ALS-1805
  ignore("first level meeting") {
    runTestForCustomDialect("visit06.yaml", "dialect.yaml", Set("about", "duration"))
  }

  test("first level meeting with properties") {
    runTestForCustomDialect("visit07.yaml", "dialect.yaml", Set("about"))
  }

  test("Custom Plugins completion Dummy") {
    val p                   = filePath("dialect.yaml")
    val editorConfiguration = EditorConfiguration().withDialect(p)
    for {
      editorConfigState <- editorConfiguration.getState
      alsConfiguration  <- Future(ALSConfigurationState(editorConfigState, EmptyProjectConfigurationState(), None))
      dialect           <- alsConfiguration.getAmfConfig.baseUnitClient().parseDialect(p)
      result <- {
        val url = filePath("visit01.yaml")
        for {
          content <- alsConfiguration.fetchContent(url)
          offset <- Future.successful {
            val fileContentsStr = content.stream.toString
            val markerInfo      = this.findMarker(fileContentsStr)

            markerInfo.offset
          }
          suggestions <- {
            val suggestions =
              new Suggestions(AlsConfiguration(), new PlatformDirectoryResolver(platform))
                .initialized()
            suggestions.completionsPluginHandler.cleanIndex()
            suggestions.completionsPluginHandler
              .registerPlugins(Seq(DummyCompletionPlugin(), DummyInvalidCompletionPlugin()), dialect.dialect.id)

            suggestions.suggest(url, offset, snippetsSupport = true, None, alsConfiguration)
          }
        } yield suggestions
      }
    } yield {
      assert(result.length == 1 && result.forall(_.documentation.getOrElse("") == "dummy description"))
    }
  }
}
