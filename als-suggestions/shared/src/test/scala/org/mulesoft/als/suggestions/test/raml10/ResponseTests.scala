package org.mulesoft.als.suggestions.test.raml10

import org.mulesoft.typesystem.definition.system.RamlResponseCodes
import org.scalatest.Assertion

import scala.concurrent.Future

class ResponseTests extends RAML10Test {

  //Ciclo de tests
  test(s"Response test") {
    this.runOrderedTest("responseCodes/responseCode01.raml", RamlResponseCodes.all.map(v => v + ":\n        ").sorted)
  }

  private def runOrderedTest(path: String, expected: Seq[String]): Future[Assertion] = {
    this.suggest(filePath(path), format, None).map { actual =>
      assert(actual.map(_.text).equals(expected))
    }
  }
}
