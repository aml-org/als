package org.mulesoft.als.suggestions.test

import org.mulesoft.als.suggestions.plugins.aml.webapi.async.runtimeexpressions.{
  AsyncAPIRuntimeExpressionParser,
  MessageBaseExpressionToken
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions.{
  OASRuntimeExpressionParser,
  RequestCodeBaseExpressionToken,
  ResponseCodeBaseExpressionToken
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.runtimeexpression.{InvalidExpressionToken, RuntimeParsingToken}
import org.scalatest.FlatSpec

class RuntimeExpressionsTest extends FlatSpec {

  behavior of "OAS Runtime Expression parsers"

  it should "identify full OAS expressions" in {
    val urlParsed        = OASRuntimeExpressionParser("$url")
    val methodParsed     = OASRuntimeExpressionParser("$method")
    val statusCodeParsed = OASRuntimeExpressionParser("$statusCode")
    val requestHeaderParsed =
      OASRuntimeExpressionParser("$request.header.token")
    val requestQueryParsed = OASRuntimeExpressionParser("$request.query.name")
    val requestPathParsed  = OASRuntimeExpressionParser("$request.path.name")
    val requestBodyParsed  = OASRuntimeExpressionParser("$request.body")
    val requestBodyWithFragmentParsed =
      OASRuntimeExpressionParser("$request.body#fragment")
    val responseHeaderParsed =
      OASRuntimeExpressionParser("$response.header.token")
    val responseQueryParsed = OASRuntimeExpressionParser("$response.query.name")
    val responsePathParsed  = OASRuntimeExpressionParser("$response.path.name")
    val responseBodyParsed  = OASRuntimeExpressionParser("$response.body")
    val responseBodyWithFragmentParsed =
      OASRuntimeExpressionParser("$response.body#fragment")

    assert(urlParsed.completelyValid)
    assert(methodParsed.completelyValid)
    assert(statusCodeParsed.completelyValid)
    assert(requestHeaderParsed.completelyValid)
    assert(requestQueryParsed.completelyValid)
    assert(requestPathParsed.completelyValid)
    assert(requestBodyParsed.completelyValid)
    assert(requestBodyWithFragmentParsed.completelyValid)
    assert(responseHeaderParsed.completelyValid)
    assert(responseQueryParsed.completelyValid)
    assert(responsePathParsed.completelyValid)
    assert(responseBodyParsed.completelyValid)
    assert(responseBodyWithFragmentParsed.completelyValid)
  }

  it should "identify invalid tokens" in {
    val urlParsed        = OASRuntimeExpressionParser("$urlx")
    val methodParsed     = OASRuntimeExpressionParser("$methodx")
    val statusCodeParsed = OASRuntimeExpressionParser("$statusCodex")
    val requestHeaderParsed =
      OASRuntimeExpressionParser("$request.headerx.token")
    val requestQueryParsed = OASRuntimeExpressionParser("$request.queryx.name")
    val requestPathParsed  = OASRuntimeExpressionParser("$request.pathx.name")
    val requestBodyParsed  = OASRuntimeExpressionParser("$request.bodyx")
    val requestBodyWithFragmentParsed =
      OASRuntimeExpressionParser("$request.bodyx#fragment")
    val responseHeaderParsed =
      OASRuntimeExpressionParser("$response.headerx.token")
    val responseQueryParsed =
      OASRuntimeExpressionParser("$response.queryx.name")
    val responsePathParsed = OASRuntimeExpressionParser("$response.pathx.name")
    val responseBodyParsed = OASRuntimeExpressionParser("$response.bodyx")
    val responseBodyWithFragmentParsed =
      OASRuntimeExpressionParser("$response.bodyx#fragment")

    assert(!urlParsed.completelyValid)
    assert(!methodParsed.completelyValid)
    assert(!statusCodeParsed.completelyValid)
    assert(!requestHeaderParsed.completelyValid)
    assert(!requestQueryParsed.completelyValid)
    assert(!requestPathParsed.completelyValid)
    assert(!requestBodyParsed.completelyValid)
    assert(!requestBodyWithFragmentParsed.completelyValid)
    assert(!responseHeaderParsed.completelyValid)
    assert(!responseQueryParsed.completelyValid)
    assert(!responsePathParsed.completelyValid)
    assert(!responseBodyParsed.completelyValid)
    assert(!responseBodyWithFragmentParsed.completelyValid)

    assert(
      checkTokens(responseBodyWithFragmentParsed,
                  Seq(
                    classOf[OASRuntimeExpressionParser],
                    classOf[ResponseCodeBaseExpressionToken],
                    classOf[InvalidExpressionToken]
                  )))
    assert(
      checkTokens(requestBodyWithFragmentParsed,
                  Seq(
                    classOf[OASRuntimeExpressionParser],
                    classOf[RequestCodeBaseExpressionToken],
                    classOf[InvalidExpressionToken]
                  )))
  }

  behavior of "AsyncAPI Runtime Expression parsers"

  it should "identify full Async expressions" in {
    val headerParsed =
      AsyncAPIRuntimeExpressionParser("$message.header#/MQMD/CorrelId")
    val payloadParsed =
      AsyncAPIRuntimeExpressionParser("$message.payload#/MQMD/CorrelId")

    assert(headerParsed.completelyValid)
    assert(payloadParsed.completelyValid)
  }

  it should "identify invalid tokens" in {
    val messageParsed    = AsyncAPIRuntimeExpressionParser("$message")
    val badTagParsed     = AsyncAPIRuntimeExpressionParser("message.")
    val incompleteParsed = AsyncAPIRuntimeExpressionParser("$message.header")

    assert(!messageParsed.completelyValid)
    assert(!badTagParsed.completelyValid)
    assert(!incompleteParsed.completelyValid)

    assert(
      checkTokens(incompleteParsed,
                  Seq(
                    classOf[AsyncAPIRuntimeExpressionParser],
                    classOf[MessageBaseExpressionToken],
                    classOf[InvalidExpressionToken]
                  )))
    assert(
      checkTokens(badTagParsed,
                  Seq(
                    classOf[AsyncAPIRuntimeExpressionParser],
                    classOf[InvalidExpressionToken]
                  )))
  }

  protected def checkTokens(root: RuntimeParsingToken, expected: Seq[Class[_ <: RuntimeParsingToken]]): Boolean = {
    expected match {
      case Nil         => false
      case head :: Nil => root.next.isEmpty && head.isInstance(root)
      case head :: tail =>
        head.isInstance(root) && root.next.exists(next => checkTokens(next, tail))
    }
  }
}
