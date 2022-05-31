package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.plugins.aml.hackathon.RestAIGenerator
import org.scalatest.AsyncFunSuite

import scala.concurrent.ExecutionContext

class RestAIGeneratorTest extends AsyncFunSuite {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("Should get the response content for a simple test") {
    for {
      result <- RestAIGenerator.generate("Say this is a test")
    } yield {
      assert(result.nonEmpty)
    }
  }

  test("Should get the response content for a more sofisticated test") {
    for {
      result <- RestAIGenerator.generate("""
          |#%RAML 1.0
          |  types:
          |    person:
          |""".stripMargin)
    } yield {
      assert(result.nonEmpty)
    }
  }
}
