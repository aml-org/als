package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.logger.{EmptyLogger, Logger}
import org.mulesoft.als.server.modules.diagnostic.custom.AMFOpaValidator
import org.scalatest.Assertion
import org.scalatest.Matchers.{fail, succeed}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class DummyAmfOpaValidator(val result: String = "{}") extends AMFOpaValidator with FileAssertionTest {
  override val logger: Logger = EmptyLogger

  private var calls: Map[String, String] = Map.empty
  private var callCount: Int             = 0

  private val promises: mutable.Queue[Promise[Int]] = mutable.Queue.empty

  override def validateWithProfile(profile: String, data: String): Future[ValidationResult] = {
    calls = calls + (profile -> data)
    callCount = callCount + 1
    promises.dequeueFirst(!_.isCompleted).foreach(_.success(callCount))
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

  def calledNTimes(n: Int): Future[Assertion] = {
    if (callCount < n) { // still did not process N'th validation
      val promise = Promise[Int]()
      promises.enqueue(promise)
//      timeoutFuture(promise.future, 3000L)
      promise.future
        .flatMap(_ => calledNTimes(n))
        .recover {
          case _ if callCount == n => succeed
          case _                   => fail()
        }
    } else if (callCount == n)
      Future.successful(succeed)
    else fail() // to many validations
  }
}
