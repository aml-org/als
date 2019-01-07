package org.mulesoft.als.suggestions.test.aml.AsyncAPI

import amf.core.remote.Aml
import org.mulesoft.als.suggestions.test.aml.DialectLevelSuggestionsTest

class AsyncAPILevelSuggestionsTest extends DialectLevelSuggestionsTest {

  private val mapTests: Map[String, (Option[String], Int)] = Map(
    "[*0]" -> (None, 1),
    "[*1]" -> (Some("InfoObject"), 2),
    "[*2]" -> (Some("LicenseObject"), 3),
    "[*3]" -> (Some("SecuritySchemeObject"), 2),
    "[*4]" -> (Some("ServerObject"), 2)
  )

  private def runLocalTest(label: String, value: (Option[String], Int)): Unit = {
    test(s"Structure Test for: ${value._1.getOrElse("Root")}, on level: ${value._2}") {
      runDialectTest("root.yaml", "file:///asyncapi/dialect.yaml", value._1, value._2, label, mapTests.keys.toArray)
    }
  }

  mapTests.foreach(v => runLocalTest(v._1, v._2))

  // add different ssuit for oas with dialects??
  override def format: String = Aml.toString

  override def rootPath: String = "AML/AsyncAPI/full"
}
