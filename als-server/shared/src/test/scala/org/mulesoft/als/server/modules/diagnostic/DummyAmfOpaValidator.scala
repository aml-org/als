package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.scalatest.Assertion
import org.scalatest.Matchers.{fail, succeed}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DummyAmfOpaValidator(val result: String = "{}") extends AMFOpaValidator with FileAssertionTest {
  override val logger: Logger = EmptyLogger

  private var calls: Map[String, String] = Map.empty
  private var callCount: Int             = 0

  override def validateWithProfile(profile: String, data: String): Future[ValidationResult] = {
    calls = calls + (profile -> data)
    callCount = callCount + 1
    Future.successful(result)
  }

  def called(profile: String, goldenUri: String): Future[Assertion] = {
    val v = calls.get(profile)
    assert(v.isDefined)
    for {
      tmp <- writeTemporaryFile(goldenUri)(v.get)
      r   <- assertDifferences(tmp, goldenUri)
    } yield r
  }

  def calledNTimes(n: Int): Assertion =
    if (callCount == n) succeed else fail()
}
