package org.mulesoft.als.suggestions.test.core.raml

import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionPlugin
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}

class BasicCoreTestsRAML extends CoreTest with DummyPlugins {

  def rootPath: String = "raml10"

  def format: String = "RAML 1.0"

  ignore("RAML protocols") {
    for {
      result <- suggest("structure/test01.raml")
    } yield assert(result.length == 1 && result.forall(d => d.description == "responses"))
  }
}
