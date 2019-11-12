package org.mulesoft.als.suggestions

import common.diff.{FileAssertionTest, ListAssertions}
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.patcher.{ColonToken, CommaToken, QuoteToken}
import org.scalatest.AsyncFunSuite

import scala.concurrent.{ExecutionContext, Future}

class ContentPatcherTest extends AsyncFunSuite with FileAssertionTest with ListAssertions {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val basePath = "file://als-suggestions/shared/src/test/resources/test/patcher"

  test("Test in json key patch") {
    val url      = basePath + "/in-key.json"
    val expected = basePath + "/in-key.result.json"
    for {
      c <- platform.resolve(url)
      patched <- Future {

        val content    = c.stream.toString
        val offset     = content.indexOf("*")
        val rawContent = content.substring(0, offset) + content.substring(offset + 1)
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, offset, Syntax.JSON)
      }
      tmp <- writeTemporaryFile(expected)(patched.content) // todo: unify code
      r   <- assertDifferences(tmp, expected)
      finalAssertion <- {
        if (r == succeed) {
          assert(patched.addedTokens, List(QuoteToken, QuoteToken))
        } else r
      }
    } yield finalAssertion
  }

  test("Test in json 2 key patch") {
    val url      = basePath + "/in-key2.json"
    val expected = basePath + "/in-key2.result.json"
    for {
      c <- platform.resolve(url)
      patched <- Future {

        val content    = c.stream.toString
        val offset     = content.indexOf("*")
        val rawContent = content.substring(0, offset) + content.substring(offset + 1)
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, offset, Syntax.JSON)
      }
      tmp <- writeTemporaryFile(expected)(patched.content)
      r   <- assertDifferences(tmp, expected)
      finalAssertion <- if (r == succeed) {
        assert(patched.addedTokens, List(ColonToken, QuoteToken, QuoteToken, CommaToken))
      } else r
    } yield finalAssertion
  }

}
