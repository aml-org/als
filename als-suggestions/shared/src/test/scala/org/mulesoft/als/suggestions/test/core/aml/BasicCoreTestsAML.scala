package org.mulesoft.als.suggestions.test.core.aml

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.remote.Spec
import org.mulesoft.als.common.PlatformDirectoryResolver
import org.mulesoft.als.configuration.AlsConfiguration
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

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

  test("Custom Plugins completion Dummy") {
    val p                = filePath("dialect.yaml")
    val amfConfiguration = AmfConfigurationWrapper()
    for {
      dialect <- amfConfiguration
        .parse(p)
        .map { r =>
          r.result.baseUnit match {
            case d: Dialect => amfConfiguration.registerDialect(d)
          }
          r
        }
        .map(_.result.baseUnit)
      result <- {
        val url = filePath("visit01.yaml")
        for {
          content <- amfConfiguration.fetchContent(url)
          offset <- Future.successful {
            val fileContentsStr = content.stream.toString
            val markerInfo      = this.findMarker(fileContentsStr)

            markerInfo.offset
          }
          suggestions <- {
            val suggestions =
              new Suggestions(AlsConfiguration(), new PlatformDirectoryResolver(amfConfiguration.platform))
                .initialized()
            suggestions.completionsPluginHandler.cleanIndex()
            suggestions.completionsPluginHandler
              .registerPlugins(Seq(DummyCompletionPlugin(), DummyInvalidCompletionPlugin()), dialect.id)

            suggestions.suggest(url, offset, snippetsSupport = true, None, amfConfiguration)
          }
        } yield suggestions
      }
    } yield {
      assert(result.length == 1 && result.forall(_.documentation.getOrElse("") == "dummy description"))
    }
  }
}
