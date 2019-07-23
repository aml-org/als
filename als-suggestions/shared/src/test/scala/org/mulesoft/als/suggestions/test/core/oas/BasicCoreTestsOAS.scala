package org.mulesoft.als.suggestions.test.core.oas

import amf.dialects.OAS20Dialect
import org.mulesoft.als.suggestions.CompletionsPluginHandler
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.test.core.{CoreTest, DummyPlugins}
import org.mulesoft.high.level.InitOptions

import scala.concurrent.Future

class BasicCoreTestsOAS extends CoreTest with DummyPlugins {

  def rootPath: String = "oas20"

  def format: String = "OAS 2.0"

  ignore("Custom Plugins completion Dummy") {

    for {
      _ <- Suggestions.init(InitOptions.AllProfiles)
      _ <- Future {
        CompletionsPluginHandler.cleanIndex()
        CompletionsPluginHandler.registerPlugins(Seq(DummyCompletionPlugin(), DummyInvalidCompletionPlugin()),
                                                 OAS20Dialect().id)
      }
      result <- suggest("structure/test01.yml")
    } yield assert(result.length == 1 && result.forall(_.description == "dummy description"))
  }

// TODO: enable when OAS20Dialect is available in JS

//  ignore("Custom Plugins completion OAS Structure info") {
//    for {
//      result <- suggest("structure/test01.yml", Seq(AMLStructureCompletionPlugin), Seq(OAS20Dialect.dialect))
//    } yield assert(result.length == 1 && result.forall(_.description == "info"))
//  }
//
//  ignore("Custom Plugins completion OAS Structure host") {
//    for {
//      result <- suggest("structure/test02.yml", Seq(AMLStructureCompletionPlugin), Seq(OAS20Dialect.dialect))
//    } yield assert(result.length == 1 && result.forall(_.description == "host"))
//  }
//
//  ignore("Custom Plugins completion OAS Structure examples") {
//    for {
//      result <- suggest("structure/test116.yml", Seq(AMLStructureCompletionPlugin), Seq(OAS20Dialect.dialect))
//    } yield assert(result.length == 1 && result.forall(_.description == "examples"))
//  }
//
//  ignore("Custom Plugins completion OAS Structure headers") {
//    for {
//      result <- suggest("structure/test117.yml", Seq(AMLStructureCompletionPlugin), Seq(OAS20Dialect.dialect))
//    } yield assert(result.length == 1 && result.forall(_.description == "headers"))
//  }
}
