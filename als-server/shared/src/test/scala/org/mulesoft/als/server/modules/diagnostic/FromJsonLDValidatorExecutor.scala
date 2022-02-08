package org.mulesoft.als.server.modules.diagnostic

import amf.aml.client.scala.model.document.DialectInstance
import amf.core.client.common.validation.UnknownProfile
import amf.core.client.scala.validation.AMFValidationReport
import amf.custom.validation.client.scala.{
  BaseProfileValidatorBuilder,
  CustomValidator,
  ProfileValidatorExecutor,
  ValidatorExecutor
}
import org.mulesoft.als.common.diff.FileAssertionTest
import org.scalatest.Assertion
import org.scalatest.Matchers.{fail, succeed}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class FromJsonLDValidatorExecutor(val result: String = "{}") extends CustomValidator with FileAssertionTest {

  private var calls: Map[String, String] = Map.empty
  private var callCount: Int             = 0

  private val promises: mutable.Queue[Promise[Int]] = mutable.Queue.empty

//  override def validateWithProfile(profile: String, data: String): Future[String] = {
//    calls = calls + (profile -> data)
//    callCount = callCount + 1
//    promises.dequeueFirst(!_.isCompleted).foreach(_.success(callCount))
//    Future.successful(result)
//  }

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

  def executedProfiles(profiles: Set[String]): Future[Assertion] = {
    if (callCount < profiles.size) { // still did not process N'th validation
      val promise = Promise[Int]()
      promises.enqueue(promise)
      //      timeoutFuture(promise.future, 3000L)
      promise.future
        .flatMap(_ => calledNTimes(profiles.size))
        .recover {
          case _ if callCount == profiles.size => checkProfiles(profiles)
          case _                               => fail()
        }
    } else if (callCount == profiles.size)
      Future.successful(checkProfiles(profiles))
    else fail() // to many validations
  }

  private def checkProfiles(profiles: Set[String]) = {
    val list = calls.keys.toList
    profiles.map(p => {
      if (list.contains(p)) true
      else fail(s"Profile $p does not match at called")
    })
    succeed
  }

  def calledAtLeastNTimes(n: Int): Future[Assertion] = {
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
    } else Future.successful(succeed)
  }

  override def validate(document: String, profile: String): Future[String] = {
    calls = calls + (profile -> document)
    callCount = callCount + 1
    promises.dequeueFirst(!_.isCompleted).foreach(_.success(callCount))
    Future.successful(result)
  }
}
class AlsBaseProfileValidatorBuilder(val jsonLDValidatorExecutor: FromJsonLDValidatorExecutor)
    extends BaseProfileValidatorBuilder {
  override def validator(profile: DialectInstance): ProfileValidatorExecutor =
    new ProfileValidatorExecutor(new ValidatorExecutor(jsonLDValidatorExecutor), profile)

}

object FromJsonLdValidatorProvider {
  def apply(result: String): AlsBaseProfileValidatorBuilder =
    new AlsBaseProfileValidatorBuilder(new FromJsonLDValidatorExecutor(result))

  def empty: AlsBaseProfileValidatorBuilder = apply(AMFValidationReport.empty("", UnknownProfile).toString)
}
