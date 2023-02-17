package org.mulesoft.als.server

import org.mulesoft.lsp.feature.{RequestHandler, RequestType}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class RequestMapTest extends AsyncFlatSpec with Matchers with OptionValues {
  case object SumRequestType extends RequestType[(Int, Int), Int]

  case object SumRequestHandler extends RequestHandler[(Int, Int), Int] {
    override def `type`: SumRequestType.type = SumRequestType

    override def apply(params: (Int, Int)): Future[Int] = Future(params._1 + params._2)
  }

  behavior of "RequestMap"
  it should "should resolve request handler" in {

    val requestMap = RequestMap.empty
      .put(SumRequestType, SumRequestHandler)

    val handler = requestMap(SumRequestType).value

    handler((5, 4))
      .map(_ should be(9))
  }
}
